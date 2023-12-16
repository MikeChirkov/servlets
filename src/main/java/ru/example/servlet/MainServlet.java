package ru.example.servlet;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.example.config.JavaConfig;
import ru.example.controller.PostController;
import ru.example.handler.Handler;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MainServlet extends HttpServlet {
    private static final Map<String, Map<String, Handler>> handlers = new ConcurrentHashMap<>();
    private static final String PATH_POST = "/api/posts";
    private static final String PATH_POST_WITH_PARAM = "/api/posts/";
    private PostController controller;

    @Override
    public void init() {
        final var context = new AnnotationConfigApplicationContext(JavaConfig.class);
        controller = context.getBean(PostController.class);

        addHandler("GET", PATH_POST, ((path, req, resp) -> controller.all(resp)));
        addHandler("GET", PATH_POST_WITH_PARAM, ((path, req, resp) -> {
            final var id = getLastItemInPath(path);
            controller.getById(id, resp);
        }));
        addHandler("POST", PATH_POST, ((path, req, resp) -> controller.save(req.getReader(), resp)));
        addHandler("DELETE", PATH_POST_WITH_PARAM, ((path, req, resp) -> {
            final var id = getLastItemInPath(path);
            controller.removeById(id, resp);
        }));
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) {
        try {
            final var path = req.getRequestURI();
            final var method = req.getMethod();

            Handler handler;
            if (path.startsWith(PATH_POST_WITH_PARAM) && path.matches(PATH_POST_WITH_PARAM + "\\d+")) {
                handler = handlers.get(method).get(PATH_POST_WITH_PARAM);
            } else {
                handler = handlers.get(method).get(path);
            }
            if (handler == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            handler.handle(path, req, resp);

        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void addHandler(String method, String path, Handler handler) {
        Map<String, Handler> map = new ConcurrentHashMap<>();
        if (handlers.containsKey(method)) {
            map = handlers.get(method);
        }
        map.put(path, handler);
        handlers.put(method, map);
    }

    private Long getLastItemInPath(String path) {
        return Long.parseLong(path.substring(path.lastIndexOf("/") + 1));
    }
}