package ru.example.repository;

import ru.example.model.Post;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PostRepositoryInMemoryImpl implements PostRepository {

    Map<Long, Post> storageMap = new ConcurrentHashMap<>();

    public List<Post> all() {
        return new ArrayList<>(storageMap.values());
    }

    public Optional<Post> getById(long id) {
        if (!storageMap.containsKey(id))
            return Optional.empty();
        return Optional.of(storageMap.get(id));
    }

    public Post save(Post post) {
        if (post.getId() == 0) {
            if (!storageMap.isEmpty())
                post.setId(Collections.max(storageMap.keySet()) + 1);
            storageMap.put(post.getId(), post);
        } else {
            if (storageMap.containsKey(post.getId())) {
                storageMap.put(post.getId(), post);
            } else {
                return null;
            }
        }
        return post;
    }

    public void removeById(long id) {
        storageMap.remove(id);
    }
}
