# User API Spec

## Register User

Endpoint: POST /api/users

Request Body :

```json
{
  "username" : "Bernhard",
  "password" : "Winner",
  "name" : "Bernhard Winner"
}
```

Response Body (Success) :

```json
{
  "data" : "OK"
}
```

Response Body (Failed) :

```json
{
  "errors": "Username must not blank, ???"
}
```

## Login User

Endpoint: POST /api/auth/login

Request Body :

```json
{
  "username" : "Bernhard",
  "password" : "Winner"
}
```

Response Body (Success) :

```json
{
  "data" : {
    "token" : "TOKEN",
    "expired_at": 121212121212 //miliseconds
  }
}
```

Response Body (Failed, 401) :

```json
{
  "errors": "username or password is wrong"
}
```

## Get User

Endpoint: GET /api/users/current

Request Header :

- X-API-TOKEN : Token (Mandatory)

Response Body (Success) :

```json
{
  "data" : {
    "username": "Bernhard",
    "name": "Bernhard Winner"
  }
}
```

Response Body (Failed, 401) :

```json
{
  "errors": "Unauthorized"
}
```

## Update User


Endpoint: PATCH /api/users/current

Request Header :

- X-API-TOKEN : Token (Mandatory)

Request Body :

```json
{
  "name" : "Bernhard Winner", //put if only want to update name
  "password" : "newPassword" //put if only want to update password
}
```

Response Body (Success) :

```json
{
  "data" : {
    "username": "Bernhard",
    "name": "Bernhard Winner"
  }
}
```

Response Body (Failed, 401) :

```json
{
  "errors": "Unauthorized"
}
```

## Logout User

Endpoint: DELETE /api/auth/logout

Request Header :

- X-API-TOKEN : Token (Mandatory)

Response Body (Success) :

```json
{
  "data" : "OK"
}
```