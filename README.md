[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-24ddc0f5d75046c5622901739e7c5dd533143b0c8e959d652212380cedb1ea36.svg)](https://classroom.github.com/a/DLC4WqXm)
# {Fernandez Fiel, Lucas Ezequiel}

https://two024-tp-entrega-2-hiogen.onrender.com

Template para TP DDS 2024 - Entrega 3

# Endpoints

# Viandas

Operaciones relacionadas con viandas.

## POST /viandas

Agregar una nueva vianda.

**Request Body:**
- **application/json**

**Example Value:**
```json
{
    "codigoQR": "asd",
    "fechaElaboracion": "2024-05-09T10:30:00Z",
    "estado": "PREPARADA",
    "colaboradorId": 10,
    "heladeraId": 5
}
```

## DELETE /viandas
Elimina las viandas de la base de datos

## GET viandas/search/findByColaboradorIdAndAnioAndMes
Obtener una vianda por id de colaborador, su anio y mes

**Request:**

-   Parameters: **colaboradorId (long) - ID del colaborador**
-   Parameters: **anio (integer) - anio de la vianda**
-   Parameters: **mes (integer) - mes de la vianda**

## GET /viandas/{qr}
Obtener una vianda por qr

**Request**

-   Parameters: **qr (string) - qr de la vianda**

## GET /viandas/{qr}/vencida
Evaluar vencimiento de una vianda llamada por qr

**Request**

-   Parameters: **qr (string) - qr de la vianda**

## PATCH /viandas/{qrVianda}
Modificar vianda llamada por qr

**Request**

-   Parameters: **qrVianda (string) - qr de la vianda**


## Schemas

## ViandaDTO
```json
{
  "id": "integer",
  "qr": "string",
  "colaboradorId": "Long",
  "heladeraId": "integer",
  "estado": "EstadoViandaEnum",
  "fechaElaboracion": "LocalDateTime"
}
```
-  **id (INTEGER)** - ID de la vianda, no se usa en el post
- **qr (STRING)** - QR de la vianda
- **colaboradorId (Long)** - Id del colaborador al que le pertenece la vianda
- **heladeraId (integer)** - Id de la heladera que contiene la vianda
- **estado (EstadoViandaEnum)** - Estado de la vianda (ASIGNADO / CREADO / EN_VIAJE / ENTREGADO)
- **fechaElaboracion (LocalDateTime)** - Fecha cuando se realizo la vianda
