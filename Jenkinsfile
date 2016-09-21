node {
    stage 'clean'
    sh "./mvnw clean"

    stage 'tests'
    sh "./mvnw test"

    stage 'packaging'
    sh "./mvnw package -Pprod -DskipTests"
}
