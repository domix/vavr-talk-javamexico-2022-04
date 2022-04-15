# Bean Validation (BV)

API para aplicar restricciones y verificar que se cumplan en un momento dado.

Existen al menos 2 versiones comunmente usadas:

1. Bean Validation 2 (`javax.validation.*`)
2. Bean Validation 3 (`jakarta.validation.*`)

Funcionan de forma identica, solo dependiendo el runtime se puede usar una o la otra.

En este directorio de codigo, existen los siguientes modulos:

1. `hibernate`: Una demo sencilla del API de BV 
2. `api`: Biblioteca con una API que abstrae el uso especifico de alguna implementacion concreta. Aqui es donde se integra con `vavr`. Ademas define un conjunto de pruebas para que los implementadores de este API validen el funcionamiento. 
3. `javax`: Implementacion concreta con BV 2
4. `jakarta`: Implementacion concreta con BV 3
