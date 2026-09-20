# Proyecto final · Semana 6 · GitHub Copilot


**Alumno:** `Karina G. Saucedo Garza` · **Usuario de GitHub:** `karisgza`

## 1. Qué construí


| | Feature | Especificación |
|---|---|---|
| [X ] | `GET /reports/progress` — avance por proyecto | [`specs/progress.md`](../specs/progress.md) |

## 2. El pull request

- **URL del PR (mergeado):** `https://github.com/karisgza/taskflow-copilot-karinasgza/pull/5`
- **Commit del merge en `main`:** `730dc28 (HEAD -> main, origin/main, origin/HEAD) Merge pull request #5 from karisgza/feature/progress`
- **Comentarios de Copilot code review:** `3`

## 3. Cómo lo hice


| Paso | Qué hice | Evidencia |
|---|---|---|
| Rama y spec | `git switch -c feature/progress` y copié la spec a `specs/` | `git log --oneline main..feature/progress` (antes del merge) | El archivo progress.md dentro de /specs
| Implementación | `copilot -p "/crear-endpoint-taskflow …"` con `gpt-5-mini` | `semana6/sesion-implementacion.md` (tiene la línea `Skill "crear-endpoint-taskflow" loaded successfully`) |
| Revisión | agente `revisor` sobre `semana6/proyecto-final.diff` | `semana6/revision.md` (termina con `Veredicto:`) |
| Tests | `mvn test` en verde | `
[INFO] Tests run: 80, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS` |
| Comprobación REST | `verificar.ps1` con `casos-<feature>.ps1` | sección 5 de este documento |
| Code review | Copilot en el PR | https://github.com/karisgza/taskflow-copilot-karinasgza/pull/5/changes |

## 4. Qué hizo el agente y qué corregí yo


| # | Qué hizo mal el agente (archivo) | Quién lo detectó | Cómo quedó corregido |
|---|---|---|---|
| 1 | `El agente pidió crear tests adicionales para progress, quería tests para los totales por separado además de los de porcentaje` | `yo` | `no se aceptó esa sugerencia en el PR` |
| 2 | `El agente tuvo algunos errores al generar el documento ARQUITECTURA.md` | `verificador` | `se le pidió al gente revisar el resultado del verificador` |


**Lo que el agente hizo bien a la primera** (una o dos líneas): 
-Implementar el spec de overdue
-Los resultados del AWS MCP y Playwright MCP fueron correctos


## 5. Comprobaciones REST


```text
App lista en 11 s.
[OK]    GET /tasks/overdue devuelve solo la tarea 7
[OK]    GET /tasks/unassigned devuelve las tareas 4 y 6
[OK]    GET /projects/1/summary
[OK]    GET /projects/2/summary
[OK]    GET /projects/3/summary
[OK]    GET /projects/99/summary responde 404
[OK]    GET /projects/1/summary sin token responde 401
[OK]    GET /reports/progress devuelve los 3 proyectos con la semilla
[OK]    El proyecto 1 se llama «Plataforma TaskFlow»
[OK]    Tras pasar la tarea 1 a DONE, el proyecto 1 queda en 2/5 = 40
[OK]    GET /reports/progress sin token responde 401
App detenida (PID 1892).
[OK]    App apagada: el puerto 8080 ya no responde
RESULTADO: 12/12 OK
```

## 6. Créditos de la semana


#El primer número lo pude sacar de nuestra comprobación los demás números de billing y están en USD ya que no tengo capturas del copilot -p para cada día. 

| Qué | AI credits |
|---|---|
| Usados en septiembre según github.com (incluye semanas anteriores si usaste Copilot antes) | `177` |
| Implementación con la skill (`AI Credits` del PF-2) | `36` |
| Revisión del `revisor` (`AI Credits` del PF-3) | `0.21 USD` |
| Correcciones del PF-4 y del PF-6, si hubo (`AI Credits`) | `0.38 USD` |
