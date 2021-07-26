package com.codeoftheweb.salvo.utils;

import com.codeoftheweb.salvo.models.Salvo;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.*;

public class Utils {

    public static Map<String, Object> getDefaultDTO(String key, Object value){
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put(key, value);
        return dto;
    }

    public static List<Salvo> fromSetToList(Set<Salvo> salvos) {
        List<Salvo> aux = new ArrayList<>();
        for (int i = 1; i <= salvos.size(); i++) {
            int finalI = i;
            Optional<Salvo> salvo = salvos.stream().filter(s -> s.getTurn() == finalI).findFirst();
            if (salvo.isPresent()) {
                aux.add(salvo.get());
            }
        }
        return aux;
    }

    public static boolean isGuest(Authentication authentication){
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }
}
