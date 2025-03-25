AWS_REGION=ca-central-1
REPO_URI=235772044469.dkr.ecr.$(AWS_REGION).amazonaws.com/java-poker
TAG=latest

build:
	docker build -t java-poker:$(TAG) .

tag:
	docker tag java-poker:$(TAG) $(REPO_URI):$(TAG)

login:
	aws ecr get-login-password --region $(AWS_REGION) | docker login --username AWS --password-stdin $(REPO_URI)

push: build tag login
	docker push $(REPO_URI):$(TAG)

deploy: push
	@echo "Successfully pushed updated image to $(REPO_URI):$(TAG)"
