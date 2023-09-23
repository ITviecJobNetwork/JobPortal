@REM create dir to store built output
mkdir dest

@REM remove image
docker image rm fashion-shop

@REM build webapp image
docker build -t fashion-shop .

@REM build webapp
docker run --rm -v %cd%\.docker\dest\shop:/app/target/shop -v %cd%\.docker\dest/shop.war:/app/target/shop.war fashion-shop

@REM stop all compose container
docker compose down

@REM build compose container
docker compose build

@REM create and start compose container
docker compose up -d

@REM open browser
start http://localhost:8080/shop