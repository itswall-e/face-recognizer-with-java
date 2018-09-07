# Face recognizer with Java

Reconocimiento facial con Java y OpenCV, utilizando entrenamiento en casacada y patrones binarios LBP como base para la identificación de rostros.

## In general

Dentro de los proyectos se creo un directorio llamado resources, en el cual almacenamos los archivos de reconocimiento en cascada y LBP, además, de almacenar imagenes y todo tipo de recursos para los proyectos.

## Projects

### 01 Basic Face Recognizer [project]

En este proyecto hacemos reconocimiento facial mediante entrenamiento (con ayuda de OpenCV como API base), el reconocimiento es muy simple, en ocasiones puede fallar (por problemas de intensidad de la luz), pero es el primer paso al reconocimiento de patrones.

Entre los objetos a detectar tenemos.-

* Rostros de frente.
* Rostros de perfil.
* Ojos.
* Sonrisas.

### 02 Draw Histograms [project]

En este proyecto dada una imagen mostramos en pantalla el histograma de la misma, el histograma se puede mostrar utilizando los tres canalas RGB o solo en escala de grises.

### Monitor detector [project]

En este proyecto se crea un patron de seguimiento al movimiento detectado al poder comparar un frame actual con uno anterior, el detector de movimiento se mejora si utilizamos una pila de frames (5 recomendados).

### Template [project]

Proyecto base con las librerias y rutas precargadas para nuevos proyectos.

### Training Face Recognizer [project]

Se utiliza como base el proyecto "Basic Face Recognizer" para poder identificar los rostros correctamente.

Como un agregado, ya se tiene implementado el algoritmo de entrenamiento para recordar patrones de reconomimiento facial de las personas mediante una etiqueta (label).

El proceso es muy simple, al iniciar el modo entrenador, el sistema pide a la persona 5 poses diferentes para tomar el frame en diferentes posiciones, con base a esto se entrena al algoritmo.

## ISSUES

Si encuentras errores en alguno de los proyectos, o la forma de optimizar o mejorar el código, puedes crear una issue para ponerte en contacto con nuestro equipo y con tu colaboración corregir el problema.

[Crear issue](https://github.com/itswall-e/face-recognizer-with-java/issues)

Muchas gracias.