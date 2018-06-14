Commands 




docker run --name mysql-ner -e MYSQL_ROOT_PASSWORD=Qazwsxedc.123 -e MYSQL_DATABASE=ip2location -e MYSQL_USER=admin -e MYSQL_PASSWORD=Qazwsxedc.123 -d mysql:5.7 		(ejecutar mysql a mano)

docker-compose up -d 		(ejecutar el docker compose, el docker-compose.yml)

version: '3'
services:

  mysql-ner:
    container_name: mysql-ner
    restart: always
    image: mysql:5.7
    environment:
      - MYSQL_USER=root
      - MYSQL_ROOT_PASSWORD=Qazwsxedc.123
      - MYSQL_DATABASE=ip2location
      - MYSQL_USER=admin 
      - MYSQL_PASSWORD=Qazwsxedc.123
    command: mysqld --lower_case_table_names=1 --skip-ssl
    volumes:
      - datavolume-ner:/var/lib/mysql       # persistence
volumes:
  datavolume-ner:



docker cp  /Users/jorge.rios/dumps/dmp0613.sql mysql-ner:/ 		(copiar el dump de la base de datos a docker)

docker exec mysql-ner /bin/sh -c 'mysql -u root -pQazwsxedc.123 < dmp0613.sql' 		(cargar la base de datos)



docker build . -t ner-services 		(construir imagen de servicios en docker, con el Dockerfile)

FROM openjdk:8
ADD target/ner-services-1.0.jar ner-services-1.0.jar
EXPOSE 9999
ENTRYPOINT ["java", "-jar", "ner-services-1.0.jar"]



docker run -p 9999:9999 --name ner-services --net ner-services_default --link mysql-ner:mysql -d ner-services 		(ejecutar servicios)

docker logs -f ner-services 	(ver logs)

docker stop ner-services 		(parar contenedor)

docker rm ner-services 		(eliminar contenedor)






