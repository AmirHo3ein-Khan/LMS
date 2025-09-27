package ir.lms.util;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class AnswerCacheService {

    private final ConcurrentHashMap<String, Object> answerCache = new ConcurrentHashMap<>();

    public void saveAnswerToCache(String key, Object answer) {
        answerCache.put(key, answer);
    }

    public Object getAnswerFromCache(String key) {
        return answerCache.get(key);
    }

    public void clearAnswerFromCache(String key) {
        answerCache.remove(key);
    }

    public void clearAllAnswers() {
        answerCache.clear();
    }
}
