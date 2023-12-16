package ru.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.example.controller.PostController;
import ru.example.repository.PostRepository;
import ru.example.repository.PostRepositoryInMemoryImpl;
import ru.example.service.PostService;

@Configuration
public class JavaConfig {
    @Bean
    public PostController postController() {
        return new PostController(postService());
    }

    @Bean
    public PostService postService() {
        return new PostService(postRepository());
    }

    @Bean
    public PostRepository postRepository() {
        return new PostRepositoryInMemoryImpl();
    }
}
