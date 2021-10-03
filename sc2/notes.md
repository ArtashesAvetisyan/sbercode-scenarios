`kubectl create namespace external-cluster`{{execute}}


istioctl -c /etc/rancher/k3s/k3s.yaml install -y --set components.egressGateways[0].name=istio-egressgateway --set components.egressGateways[0].enabled=true --set meshConfig.accessLogFile=/dev/stdout --set meshConfig.outboundTrafficPolicy.mode=REGISTRY_ONLY --set values.pilot.resources.requests.memory=128Mi --set values.pilot.resources.requests.cpu=50m --set values.global.proxy.resources.requests.cpu=10m --set values.global.proxy.resources.requests.memory=32Mi
