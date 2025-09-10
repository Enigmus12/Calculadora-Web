# Calculadora-Web
La solución consta de un servidor backend que responde a solicitudes HTTP GET de la Facade, un servidor Facade que responde a solicitudes HTTP GET del cliente , y un cliente Html+JS que envía los comandos y muestra las respuestas. El cliente HTML+Js hace llamadas asíncronas a la fachada.

## Estructura
    *  BackendServer : gestiona los datos y calculos de la calculadora como: 
        + mantiene una lista de numeros en memoria (linkedlist<double>)
        + permite agregar numeros (/add)
        + listar los numero (/list)
        + borrar la lista (/clear)
        + calcular la media y desviacion estandar (/stats)

        Funciona de la siguiente manera:
        + escucha las conexiones por el puerto 9001
        + devuelve resultado en formato json 
    
    * FacadeServer : actua como intermediario entre el ciente (navegador) y el backend 
        Funciona de la siguiente manera:
        + escucha en el puerto 9000
        + devuelve la pagina web de la calculadora en /cliente 
        + reenvia las demas peticiones (/add, /list, /clear, /stats) al backend y devuelve la respuesta JSON al navegador

## Ejecutar y compilar 

1. clone el repositorio
    https://github.com/Enigmus12/Calculadora-Web.git

2. acceda a Practica1 
    cd Practica1

3. compile 
    mcn clean install

4. ejecute en una terminal el BackendServer
    java -cp target/classes edu.eci.co.BackendServer

5. Ejecute en otra terminal la clase de FacadeServer
    java -cp target/classes edu.eci.co.FacadeServer 

6. abre el siguiente link 
    http://localhost:9000/cliente

