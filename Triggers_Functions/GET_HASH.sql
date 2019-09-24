--hashes the input in order to ensure some level of password security. Actual password is not stored in database
create or replace FUNCTION GET_HASH (p_email IN VARCHAR2,
                                     p_password IN VARCHAR2)
  RETURN VARCHAR2 AS 
  l_salt varchar2(30) := 'LehighUniversity';

BEGIN
    return dbms_obfuscation_toolkit.md5(input_string => p_email || l_salt || p_password);
END GET_HASH;