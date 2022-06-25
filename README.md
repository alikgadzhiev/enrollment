Enrollment task for yandex java/python course

1. Clone project 
    ```shell
    git clone https://github.com/alikgadzhiev/enrollment.git
    ```
2. Go to project dir
   ```shell
        cd enrollment
    ```
3. Compile the project
    ```shell
    mvn clean package
    ```
4. Build docker image
    ```bash
    docker build -t IMAGE_NAME .
    ```
5. Run container
    ```shell
    docker run -p 8080:80 IMAGE_NAME
    ```
