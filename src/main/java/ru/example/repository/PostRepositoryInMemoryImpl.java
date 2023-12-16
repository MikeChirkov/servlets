package ru.example.repository;

import org.springframework.stereotype.Repository;
import ru.example.model.Post;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class PostRepositoryInMemoryImpl implements PostRepository {

    private static final Map<Long, Post> storageMap = new ConcurrentHashMap<>();
    private static AtomicLong counter = new AtomicLong(0);

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
            if (!storageMap.isEmpty()) {
                post.setId(counter.intValue() + 1);
                counter = new AtomicLong(counter.longValue() + 1);
            }
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
