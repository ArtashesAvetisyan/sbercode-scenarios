Цель данного упражнения заключается в исполнении следующего сценария:

1) Установка трех сервисов: ServiceA, ServiceB, ServiceC. 

2) Настройка маршрутизации входящего трафика service mesh в ServiceA

3) Направление исходящих запросов из ServiceA в ServiceB.

4) Расщепление трафика и направление запросов из ServiceA, как в ServiceB так и в ServiceC.

5) Открытие исходящего трафика из service mesh для получения ответов из worldtimeapi.org на запросы из ServiceC.

6) Перевод 100% запросов из ServiceA в ServiceC.

ServiceA при получении запроса на адрес http://localhost:8081/, для формирования ответа запрашивает информацию у некого поставщика по константному адресу http://producer-internal-host:80/, получив ответ, ServiceA включает его в ответ на вызов из-вне кластера и возвращает его. В качестве подобных поставщиков вступают ServiceB, который всегда возвращает ответ "Hello from ServiceB", и ServiceC, который получив запрос, в свою очередь, совершает запрос на внешний хост worldtimeapi.org (http://worldtimeapi.org/api/timezone/Europe) и возвращает полученный ответ. ServiceB ожидает запросы на адрес http://localhost:8082/, ServiceC - http://localhost:8083/.

Все изменения в направлениях маршрутизации будут происходит исключительно конфигурацией service mesh при помощи API Istio и прозрачно для установленных сервисов.

Исходный код приложений:

`https://github.com/avsinsight/katacoda-scenarios/tree/main/apps`{{copy}}

Перейдем к следующему шагу.