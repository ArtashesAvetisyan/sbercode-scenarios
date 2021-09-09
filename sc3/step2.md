В данном упражнении мы установим ServiceG.

Этот сервис при получении запроса на адрес http://localhost:8083/, для формирования ответа, запрашивает информацию у поставщика по адресу http://www.oracle.com/index.html, получив ответ, возвращает данные в своем ответе.

Исходный код приложения:
`https://github.com/avsinsight/katacoda-scenarios/tree/main/apps/ServiceG`{{copy}}

Итак, бизнес сервис совершает GET запросы на внешний поставщик по адресу: `http://www.oracle.com/index.html`

Давайте рассмотрим ответ подобного запроса:
`curl -v http://www.oracle.com/index.html`{{execute}}

Среди HTTP заголовков ответа:
`< HTTP/1.1 301 Moved Permanently`
`< Server: AkamaiGHost`
`< Content-Length: 0`
`< Location: https://www.oracle.com/index.html`

301 Moved Permanently и Location: https://www.oracle.com/index.html, тела ответа нет. Т. е. oracle.com не предоставляет ни какой полезной информации по HTTP протоколу.

Давайте теперь совершим тот же запрос через HTTPS протокол:
`curl -v https://www.oracle.com/index.html`{{execute}}

В теле ответа можем увидеть html-страницу.

Суть данного упражнения заключается в создании конфигураций для envoy-прокси при помощи Istio, которые бы позволили зашифровать исходящие из бизнес сервиса небезопасные HTTP сообщения и подключиться к поставщику данных через HTTPS протокол.

Для этого:

1) Установим ServiceG

2) Откроем входящий трафик в service mesh и направим его в ServiceG

3) Откроем исходящего трафика из service mesh для получения ответов из oracle.com по HTTPS протоколу на основе исходного не зашифрованного HTTP трафика.

Перейдем к следующему шагу.



