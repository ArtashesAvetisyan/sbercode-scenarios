
`kubectl create namespace external-cluster`{{execute}}

`kubectl label namespace external-cluster istio-injection=enabled`{{execute}}

`kubectl apply -f service-ext-deployment.yml -n external-cluster`{{execute}}

`kubectl apply -f service-ext-srv.yml -n external-cluster`{{execute}}

`kubectl apply -f service-ext-gw.yml -n external-cluster`{{execute}}

`kubectl apply -f inbound-to-service-ext-vs.yml -n external-cluster`{{execute}}

`kubectl get pods --all-namespaces`{{execute}}

`curl -v http://$GATEWAY_URL/service-ext`{{execute}}