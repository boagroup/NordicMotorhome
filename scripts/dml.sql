/* Since we have not "hard-coded" anything, DMl is not really necessary to get the project running :)

   The only scenario where you might need to query the database manually is if all data is erased and no users exist,
   which would make it impossible to log in. Here is an INSERT statement to remedy that edge scenario: */

INSERT INTO users (staff_id, username, admin, password) VALUES (1, 'admin', 1, AES_ENCRYPT('admin', '-->decryption_key_goes_here<--'));
-- This would create a user "admin" with password "admin", provided you fit in the decryption key where indicated