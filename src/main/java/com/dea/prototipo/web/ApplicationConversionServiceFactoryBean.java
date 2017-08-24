package com.dea.prototipo.web;

import com.dea.prototipo.domain.WarehouseData;
import com.dea.prototipo.domain.User;
import com.dea.prototipo.domain.Warehouse;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.support.FormattingConversionServiceFactoryBean;
import org.springframework.roo.addon.web.mvc.controller.converter.RooConversionService;

/**
 * A central place to register application converters and formatters.
 */
@RooConversionService
@Configurable
public class ApplicationConversionServiceFactoryBean extends FormattingConversionServiceFactoryBean {

    @Override
    protected void installFormatters(FormatterRegistry registry) {
        super.installFormatters(registry);
        // Register application converters and formatters
    }

    public Converter<Warehouse, String> getWarehouseToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<Warehouse, java.lang.String>() {
            public String convert(Warehouse warehouse) {
                return new StringBuilder().append(warehouse.getName()).toString();
            }
        };
    }

    public Converter<Long, Warehouse> getIdToWarehouseConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.Long, Warehouse>() {
            public Warehouse convert(java.lang.Long id) {
                return Warehouse.findWarehouse(id);
            }
        };
    }

    public Converter<String, Warehouse> getStringToWarehouseConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, Warehouse>() {
            public Warehouse convert(String id) {
                return getObject().convert(getObject().convert(id, Long.class), Warehouse.class);
            }
        };
    }

    public Converter<WarehouseData, String> getDatosToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<WarehouseData, java.lang.String>() {
            public String convert(WarehouseData warehouseData) {
                return new StringBuilder().append(warehouseData.getSquareMeters()).append(' ').append(warehouseData.getInputTotalInvestment()).append(' ').append(warehouseData.getTotalWorkforce()).append(' ').append(warehouseData.getOutputAccumulation()).append(' ').append(warehouseData.getOutputStorage()).toString();
            }
        };
    }

    public Converter<Long, WarehouseData> getIdToDatosConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.Long, WarehouseData>() {
            public WarehouseData convert(java.lang.Long id) {
                return WarehouseData.findWarehouseData(id);
            }
        };
    }

    public Converter<String, WarehouseData> getStringToDatosConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, WarehouseData>() {
            public WarehouseData convert(String id) {
                return getObject().convert(getObject().convert(id, Long.class), WarehouseData.class);
            }
        };
    }

    public Converter<User, String> getUserToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<User, java.lang.String>() {
            public String convert(User user) {
                return new StringBuilder().append(user.getPassword()).append(' ').append(user.getName()).append(' ').append(user.getEmail()).toString();
            }
        };
    }

    public Converter<Long, User> getIdToUserConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.Long, User>() {
            public User convert(java.lang.Long id) {
                return User.findUser(id);
            }
        };
    }

    public Converter<String, User> getStringToUserConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, User>() {
            public User convert(String id) {
                return getObject().convert(getObject().convert(id, Long.class), User.class);
            }
        };
    }

    public void installLabelConverters(FormatterRegistry registry) {
        registry.addConverter(getWarehouseToStringConverter());
        registry.addConverter(getIdToWarehouseConverter());
        registry.addConverter(getStringToWarehouseConverter());
        registry.addConverter(getDatosToStringConverter());
        registry.addConverter(getIdToDatosConverter());
        registry.addConverter(getStringToDatosConverter());
        registry.addConverter(getUserToStringConverter());
        registry.addConverter(getIdToUserConverter());
        registry.addConverter(getStringToUserConverter());
    }

    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        installLabelConverters(getObject());
    }
}
