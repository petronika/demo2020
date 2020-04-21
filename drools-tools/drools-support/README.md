# drools-support

Утилиты для работы с Drools

## Состав

- `ru.galaktika.eim.drools.support.DroolsTemplate` - базовые методы работы с Drools,
	инкапсулирует работу с сессиями, паттерн "Шаблонный метод"
- `ru.galaktika.eim.drools.support.resource.loader.*` - загрузчики ресурсов правил Drools из разных источников,
	могут составлять цепочку, паттерн "Цепочка обязанностей"