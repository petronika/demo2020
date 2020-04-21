# drools-spring-boot-starter

Стартер для подключения `drools-support` к проекту Spring Boot

## Состав

- `ru.galaktika.eim.drools.autoconfigure.DroolsSupportAutoConfiguration` - класс автоконфигурации Spring Boot
- `ru.galaktika.eim.drools.autoconfigure.DroolsSupportProperties` - класс свойств стартера
- `ru.galaktika.eim.drools.autoconfigure.AvailableForRules` - аннотация для отметки бинов Spring,
	которые следует сделать доступными в сессии Drools