package com.dea.prototipo.web;

import javax.persistence.EntityNotFoundException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;

import com.dea.prototipo.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.authentication.encoding.MessageDigestPasswordEncoder;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service("databaseAuthenticationProvider")
public class DatabaseAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {
	private String adminUser;
	private String adminPassword;

	@Autowired
	private MessageDigestPasswordEncoder messageDigestPasswordEncoder;

	public void setAdminUser(String adminUser) {
		this.adminUser = adminUser;
	}

	public void setAdminPassword(String adminPassword) {
		this.adminPassword = adminPassword;
	}

	@Override
	protected void additionalAuthenticationChecks(UserDetails arg0,UsernamePasswordAuthenticationToken arg1) throws AuthenticationException {
		return;
	}

	@Override
	protected UserDetails retrieveUser(String username,UsernamePasswordAuthenticationToken authentication)throws AuthenticationException {
		String password = (String) authentication.getCredentials();
	    if (! StringUtils.hasText(password)) {
	    	throw new BadCredentialsException("Please enter password");
	    }

	    String encryptedPassword = messageDigestPasswordEncoder.encodePassword(password, null);
	    String expectedPassword = null;
	    User user = new User();
	    if (adminUser.equals(username)) {
	    	user.setName(username);
	    	user.setEnabled(true);
	    	expectedPassword = adminPassword;
    		if (! encryptedPassword.equals(expectedPassword))
    			throw new BadCredentialsException("Invalid password");
    		user.setPassword(expectedPassword);
	    } 
	    else {
	    	try {
	    		TypedQuery<User> query= User.findUsersByEmailEquals(username);
	    		user = query.getSingleResult();
    			if(user != null){
			        expectedPassword = user.getPassword();
			        if (! StringUtils.hasText(expectedPassword)) {
			          throw new BadCredentialsException("No password for " + username + " set in database, contact administrator");
			        }
			        if (! encryptedPassword.equals(expectedPassword)) {
			          throw new BadCredentialsException("User o password incorrento");
			        }
    			}
    			else
    				throw new BadCredentialsException("User o password incorrento");
    		} catch (EmptyResultDataAccessException e) {
		        throw new BadCredentialsException("User o password incorrento");
    		} catch (EntityNotFoundException e) {
    			throw new BadCredentialsException("User o password incorrento");
    		} catch (NonUniqueResultException e) {
    			throw new BadCredentialsException("Non-unique user, contact administrator");
    		}
	    }

	    return user;
	}
}
