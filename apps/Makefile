.PHONY:

service_a:
	cd ServiceA && docker build -f Dockerfile -t service-a:$(SRV_VERSION) .
	docker tag service-a:$(SRV_VERSION) $(REGISTRY)/service-a:$(SRV_VERSION)
	docker push $(REGISTRY)/service-a:$(SRV_VERSION)

service_b:
	cd ServiceB && docker build -f Dockerfile -t service-b:$(SRV_VERSION) .
	docker tag service-b:$(SRV_VERSION) $(REGISTRY)/service-b:$(SRV_VERSION)
	docker push $(REGISTRY)/service-b:$(SRV_VERSION)

service_c:
	cd ServiceC && docker build -f Dockerfile -t service-c:$(SRV_VERSION) .
	docker tag service-c:$(SRV_VERSION) $(REGISTRY)/service-c:$(SRV_VERSION)
	docker push $(REGISTRY)/service-c:$(SRV_VERSION)

service_g:
	cd ServiceG && docker build -f Dockerfile -t service-g:$(SRV_VERSION) .
	docker tag service-g:$(SRV_VERSION) $(REGISTRY)/service-g:$(SRV_VERSION)
	docker push $(REGISTRY)/service-g:$(SRV_VERSION)

service_ext:
	cd ExternalService && docker build -f Dockerfile -t service-ext:$(SRV_VERSION) .
	docker tag service-ext:$(SRV_VERSION) $(REGISTRY)/service-ext:$(SRV_VERSION)
	docker push $(REGISTRY)/service-ext:$(SRV_VERSION)

service_ext_sec:
	cd SecureExternalService && docker build -f Dockerfile -t sec-service-ext:$(SRV_VERSION) .
	docker tag sec-service-ext:$(SRV_VERSION) $(REGISTRY)/sec-service-ext:$(SRV_VERSION)
	docker push $(REGISTRY)/sec-service-ext:$(SRV_VERSION)


all: service_a service_b service_c service_g service_ext service_ext_sec