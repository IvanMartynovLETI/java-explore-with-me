package ru.practicum.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatisticsDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class StatisticsClient extends BaseClient {
    @Autowired
    public StatisticsClient(@Value("${statistics-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build()
        );
    }

    public void saveStatistics(HitDto hitDto) {
        post(hitDto);
    }

    public List<StatisticsDto> getStatistics(LocalDateTime start, LocalDateTime end, List<String> uris,
                                             boolean unique) {
        String strOfUris = String.join(",", uris);

        Map<String, Object> parameters = Map.of("start", start, "end", end, "uris", strOfUris, "unique",
                unique);

        ResponseEntity<Object> responseEntity = get(parameters);

        if (responseEntity.getBody() == null || responseEntity.getBody().toString().equals("[]")) {
            return new ArrayList<>();

        } else {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.convertValue(responseEntity.getBody(), new TypeReference<>() {
            });
        }
    }
}