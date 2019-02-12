#!/bin/bash

set -e

export AIT_ENVIRONMENT=production
export AIT_SERVICE=gocd
selector="fullname=${AIT_SERVICE}-${AIT_ENVIRONMENT}"
container_name="${AIT_SERVICE}"
namespace=${AIT_ENVIRONMENT}
podname=$(kubectl -n ${namespace} get pods --selector=${selector} -o jsonpath='{.items[*].metadata.name}')

set -x

kubectl -n "${AIT_ENVIRONMENT}" exec ${podname} -c ${container_name} -- bash -c 'rm /var/lib/go-server/plugins/external/gocd-elastic-agent-openstack-*.jar'
kubectl cp ./target/libs/gocd-elastic-agent-openstack-*.jar "${namespace}/${podname}:/var/lib/go-server/plugins/external/" -c ${container_name}
kubectl -n "${AIT_ENVIRONMENT}" exec ${podname} -c ${container_name} -- bash -c 'chown go:go /var/lib/go-server/plugins/external/gocd-elastic-agent-openstack-*.jar'
