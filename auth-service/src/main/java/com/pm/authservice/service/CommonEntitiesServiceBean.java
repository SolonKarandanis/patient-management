package com.pm.authservice.service;

import com.pm.authservice.config.ServiceConfigProperties;
import com.pm.authservice.dto.ApplicationConfigDTO;
import com.pm.authservice.dto.PublicConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class CommonEntitiesServiceBean  implements CommonEntitiesService {

    protected ApplicationConfigDTO getEmptyApplicationConfigDTO() {
        return new ApplicationConfigDTO();
    }

    @Override
    public ApplicationConfigDTO getApplicationConfig(){
        ApplicationConfigDTO appConfigDto = getEmptyApplicationConfigDTO();
        Map<String, Field> dtoFieldsMap = getDtoFieldsMap(appConfigDto);
        setApplicationConfigValues(appConfigDto, dtoFieldsMap);
        return appConfigDto;
    }


    protected Map<String, Field> getDtoFieldsMap(ApplicationConfigDTO appConfigDto) {
        Map<String, Field> dtoFieldsMap = new HashMap<>();
        Field[] dtoFields = appConfigDto.getClass().getFields();
        for (Field field : dtoFields) {
            dtoFieldsMap.put(field.getName(), field);
        }
        return dtoFieldsMap;
    }

    protected Field[] getConfigFields() {
        return ServiceConfigProperties.class.getFields();
    }



    protected void setApplicationConfigValues(ApplicationConfigDTO appConfigDto, Map<String, Field> mapValues) {
        try {
            Field[] configFields = getConfigFields();
            for (Field field : configFields) {
                String fieldName = field.getName();
                Object value = field.get(fieldName);
                Field dtoField = mapValues.get(fieldName);
                if (dtoField != null) {
                    ReflectionUtils.setField(dtoField, appConfigDto, value);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public PublicConfiguration getPublicApplicationConfig() {
        PublicConfiguration publicConfig = getEmptyPublicApplicationConfigDTO();
        Map<String, Field> publicDtoFieldsMap = new HashMap<>();
        Field[] dtoFields = publicConfig.getClass().getFields();
        for (Field field : dtoFields) {
            publicDtoFieldsMap.put(field.getName(), field);
        }

        try {
            Field[] configFields = getConfigFields();
            for (Field field : configFields) {
                String fieldName = field.getName();
                Object value = field.get(fieldName);
                Field dtoField = publicDtoFieldsMap.get(fieldName);
                if (dtoField != null) {
                    ReflectionUtils.setField(dtoField, publicConfig, value);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return publicConfig;
    }

    protected PublicConfiguration getEmptyPublicApplicationConfigDTO() {
        return new PublicConfiguration();
    }


}
