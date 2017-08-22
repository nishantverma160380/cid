package org.common.orika.mapper.config;

import ma.glasnost.orika.MapperFactory;

public interface MappingConfigurer {
	void configure(MapperFactory factory);
}
