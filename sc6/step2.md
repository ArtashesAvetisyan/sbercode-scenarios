На этом шаге мы установим ServiceA и ServiceB, откроем входящий трафик и направим исходящие запросы из ServiceA в ServiceB. 

На схеме это выглядет слудующим образом:

![Mesh configuration](../assets/sc2-2.png)

Давайте установим ServiceA:
`kubectl apply -f serviceA-v1-deployment.yml`{{execute}}

Применим Service для деплоймента выше:
`kubectl apply -f serviceA-srv.yml`{{execute}}

Создадим Gateway:
`kubectl apply -f serviceA-gw.yml`{{execute}}

Определим правило маршрутизации:
`kubectl apply -f inbound-to-serviceA-vs.yml`{{execute}}

Давайте установим ServiceB:
`kubectl apply -f service-b-deployment.yml`{{execute}}

Применим манифест Service для service-b-deployment:
`kubectl apply -f producer-internal-host.yml`{{execute}}

Применим правило маршрутизации запросов из ServiceA в ServiceB:
`kubectl apply -f producer-internal-host-vs.yml`{{execute}}

Подробно тип манифестов выше рассмотрены в упражнении: `https://dev2-84.pcbltools.ru/ui/ArtashesAvetisyan/sc1/`{{copy}} и `https://dev2-84.pcbltools.ru/ui/ArtashesAvetisyan/sc2/`{{copy}}

Исходный код приложений:
`https://github.com/ArtashesAvetisyan/sbercode-scenarios/tree/master/apps`{{copy}}

Проверим готовность подов:
`kubectl get pods --all-namespaces`{{execute}}

Все поды должны иметь статус Running, дождитесь нужного статсуса.


Совершим GET запрос по адресу ingress-шлюза:
`curl -v http://$GATEWAY_URL/service-a`{{execute}}

В случае успеха ответ на совершенный вызов должен быть таким:
`Hello from ServiceA! Calling Producer Service... Received response from Producer Service: Hello from ServiceB!`

Перейдем далее.