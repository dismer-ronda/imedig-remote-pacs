#git pull 

cd imedig-common
mvn clean install

cd ../imedig-core
mvn clean install

cd ../imedig-services
mvn clean install

cd ../imedig-cloud
mvn clean install

cd ../imedig-viewer
mvn clean install

cd ..
