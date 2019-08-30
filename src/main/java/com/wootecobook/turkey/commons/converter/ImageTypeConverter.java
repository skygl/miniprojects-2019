package com.wootecobook.turkey.commons.converter;

import com.wootecobook.turkey.user.service.ImageType;
import org.springframework.core.convert.converter.Converter;

import java.util.Arrays;

public class ImageTypeConverter implements Converter<String, ImageType> {

    @Override
    public ImageType convert(final String source) {
        return Arrays.stream(ImageType.values())
                .filter(type -> type.equals(source))
                .findFirst()
                .orElse(null);
    }
}
