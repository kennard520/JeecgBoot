-- Converted from MySQL to DM8.
-- Source: sas升级脚本.sql
-- MySQL table and column name case is preserved by quoted identifiers.

CREATE TABLE "oauth2_registered_client" (
  "id" VARCHAR2(100) NOT NULL,
  "client_id" VARCHAR2(100) NOT NULL,
  "client_id_issued_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  "client_secret" VARCHAR2(200) NULL,
  "client_secret_expires_at" TIMESTAMP NULL,
  "client_name" VARCHAR2(200) NOT NULL,
  "client_authentication_methods" VARCHAR2(1000) NOT NULL,
  "authorization_grant_types" VARCHAR2(1000) NOT NULL,
  "redirect_uris" VARCHAR2(1000) NULL,
  "post_logout_redirect_uris" VARCHAR2(1000) NULL,
  "scopes" VARCHAR2(1000) NOT NULL,
  "client_settings" VARCHAR2(2000) NOT NULL,
  "token_settings" VARCHAR2(2000) NOT NULL,
  CONSTRAINT "pk_oauth2_registered_client" PRIMARY KEY ("id")
);

INSERT INTO "oauth2_registered_client"
("id",
"client_id",
"client_id_issued_at",
"client_secret",
"client_secret_expires_at",
"client_name",
"client_authentication_methods",
"authorization_grant_types",
"redirect_uris",
"post_logout_redirect_uris",
"scopes",
"client_settings",
"token_settings")
VALUES
('3eacac0e-0de9-4727-9a64-6bdd4be2ee1f',
'jeecg-client',
CURRENT_TIMESTAMP,
'secret',
NULL,
'3eacac0e-0de9-4727-9a64-6bdd4be2ee1f',
'client_secret_basic',
'refresh_token,authorization_code,password,app,phone,social',
'http://127.0.0.1:8080/jeecg-',
'http://127.0.0.1:8080/',
'*',
'{"@class":"java.util.Collections$UnmodifiableMap","settings.client.require-proof-key":false,"settings.client.require-authorization-consent":true}',
'{"@class":"java.util.Collections$UnmodifiableMap","settings.token.reuse-refresh-tokens":true,"settings.token.id-token-signature-algorithm":["org.springframework.security.oauth2.jose.jws.SignatureAlgorithm","RS256"],"settings.token.access-token-time-to-live":["java.time.Duration",300000.000000000],"settings.token.access-token-format":{"@class":"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat","value":"self-contained"},"settings.token.refresh-token-time-to-live":["java.time.Duration",3600.000000000],"settings.token.authorization-code-time-to-live":["java.time.Duration",300000.000000000],"settings.token.device-code-time-to-live":["java.time.Duration",300000.000000000]}');

COMMIT;
