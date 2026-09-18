El unit verifica los porcentajes, pero no los conteos totalTasks y doneTasks que calcula este método. Una regresión en cualquiera de esos campos (o en el mapeo al DTO) podría pasar; añade aserciones para los tres proyectos.
src/test/java/com/taskflow/unit/ProgresoProyectosServiceTest.java
Línea 69

* percentDone: porcentaje completado, redondeado a 1 decimales
src/main/java/com/taskflow/dto/ProjectProgressResponse.java
Línea 9

Aquí debe decir «dados los conteos»; «dado los conteos» tiene una discordancia gramatical en el Javadoc nuevo del mapper.
src/main/java/com/taskflow/mapper/ProjectMapper.java
Línea 33
