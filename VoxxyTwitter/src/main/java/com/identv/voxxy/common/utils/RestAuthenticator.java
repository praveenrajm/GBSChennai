package com.identv.voxxy.common.utils;

import com.identv.voxxy.dao.ServiceKeyDao;
import com.identv.voxxy.dao.UserDao;
import com.identv.voxxy.dto.ServiceKeyInfo;
import com.identv.voxxy.dto.UserLoginInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.security.auth.login.LoginException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Praveen on 04-01-2016.
 */
@Component("restAuthenticator")
public final class RestAuthenticator {

    private static final Logger log = LoggerFactory.getLogger(RestAuthenticator.class);
    //  private static RestAuthenticator restAuthenticator = null;
    private UserDao userDao;
    private ServiceKeyDao serviceKeyDao;

    // A user storage which stores <username, password>
    private final Map<String, String> usersStorage = new HashMap();

    // A service key storage which stores <service_key, username>
    private final Map<String, String> serviceKeysStorage = new HashMap();

    // An authentication token storage which stores <service_key, auth_token>.
    private final Map<String, String> authorizationTokensStorage = new HashMap();


    // @Autowired
    //private UserRepository userRepository;
    public RestAuthenticator() {
        System.out.println("value  present" + userDao);
    }

    public RestAuthenticator(UserDao userDao, ServiceKeyDao serviceKeyDao) {
        this.userDao = userDao;
        this.serviceKeyDao = serviceKeyDao;
        List<UserLoginInfo> users = userDao.findAll();
        if (users != null) {
            for (UserLoginInfo userLoginInfo : users) {
                System.out.println("sdf" + userLoginInfo);
                usersStorage.put(userLoginInfo.getUsername(), userLoginInfo.getPassword());
            }
        }
        List<ServiceKeyInfo> serviceKeyInfos = serviceKeyDao.findAll();
        if (serviceKeyInfos != null) {
            for (ServiceKeyInfo serviceKeyInfo : serviceKeyInfos) {
                System.out.println("sdf" + serviceKeyInfo);
                serviceKeysStorage.put(serviceKeyInfo.getServicekey(), serviceKeyInfo.getUsername());
            }
        }

    }

   /* public static RestAuthenticator getInstance() {
        if (authenticator == null) {
            authenticator = new RestAuthenticator();
        }

        return authenticator;
    }
*/

    public String login(String serviceKey, String username, String password) throws LoginException {
        if (serviceKeysStorage.containsKey(serviceKey)) {
            String usernameMatch = serviceKeysStorage.get(serviceKey);

            if (usernameMatch.equals(username) && usersStorage.containsKey(username)) {
                String passwordMatch = usersStorage.get(username);

                if (passwordMatch.equals(password)) {

                    /**
                     * Once all params are matched, the authToken will be
                     * generated and will be stored in the
                     * authorizationTokensStorage. The authToken will be needed
                     * for every REST API invocation and is only valid within
                     * the login session
                     */

                    String authToken = UUID.randomUUID().toString();
                    //check whether auth token already generated for this user. If yes, then replace it with new one

                    for (Map.Entry<String, String> e : authorizationTokensStorage.entrySet()) {
                        if (e.getValue().equals(username))
                            authorizationTokensStorage.remove(e.getKey());
                    }
                    authorizationTokensStorage.put(authToken, username);
                    return authToken;
                }
            }
        }

        throw new LoginException("Don't Come Here Again!");
    }

    /**
     * The method that pre-validates if the client which invokes the REST API is
     * from a authorized and authenticated source.
     *
     * @param serviceKey The service key
     * @param authToken  The authorization token generated after login
     * @return TRUE for acceptance and FALSE for denied.
     */
    public boolean isAuthTokenValid(String serviceKey, String authToken) {
        if (isServiceKeyValid(serviceKey)) {
            String usernameMatch1 = serviceKeysStorage.get(serviceKey);

            if (authorizationTokensStorage.containsKey(authToken)) {
                String usernameMatch2 = authorizationTokensStorage.get(authToken);

                if (usernameMatch1.equals(usernameMatch2)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * This method checks is the service key is valid
     *
     * @param serviceKey
     * @return TRUE if service key matches the pre-generated ones in service key
     * storage. FALSE for otherwise.
     */
    public boolean isServiceKeyValid(String serviceKey) {
        return serviceKeysStorage.containsKey(serviceKey);
    }

    public void logout(String serviceKey, String authToken) throws GeneralSecurityException {
        if (serviceKeysStorage.containsKey(serviceKey)) {
            String usernameMatch1 = serviceKeysStorage.get(serviceKey);

            if (authorizationTokensStorage.containsKey(authToken)) {
                String usernameMatch2 = authorizationTokensStorage.get(authToken);

                if (usernameMatch1.equals(usernameMatch2)) {

                    /**
                     * When a client logs out, the authentication token will be
                     * remove and will be made invalid.
                     */
                    authorizationTokensStorage.remove(authToken);
                    return;
                }
            }
        }

        throw new GeneralSecurityException("Invalid service key and authorization token match.");
    }
}

