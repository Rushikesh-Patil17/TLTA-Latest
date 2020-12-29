package com.capgemini.tlta.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.capgemini.tlta.exception.RegisterUserException;
import com.capgemini.tlta.model.RegisterUser;
import com.capgemini.tlta.sevice.RegisterUserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * The Class RegisterUserController.
 */
@Api
@RestController
@RequestMapping("/api/users")
public class RegisterUserController {
	@Autowired(required = false)
	@Qualifier(value = "registerUserService")
	private RegisterUserService userService;

	/**
	 * Gets the register user by id.
	 *
	 * @param id the id
	 * @return the register user by id
	 */
	// http://localhost:8081/springfox/api/users/1
	@ApiOperation(value = "Get user By Id", 
			response = RegisterUser.class, 
			tags = "get-register-user", 
			consumes = "userId", 
			httpMethod = "GET")
	@GetMapping("/{id}")
	public ResponseEntity<RegisterUser> getRegisterUserById(@PathVariable Integer id) {
		try {
			RegisterUser user = userService.getUserById(id);
			if(user == null)
				throw new RegisterUserException();
			return new ResponseEntity<>(user, HttpStatus.OK);
		} catch (RegisterUserException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	/**
	 * Gets the all register users.
	 *
	 * @return the all register users
	 */
	// http://localhost:8081/springfox/api/users/
	@ApiOperation(value = "Get All users", 
			response = RegisterUser.class, 
			tags = "get-RegisterUser", 
			httpMethod = "GET")

	@GetMapping("/")
	public ResponseEntity<List<RegisterUser>> getAllRegisterUsers() {
		try {
			List<RegisterUser> usersList = userService.getAllRegisteredUser();
			if(usersList == null)
				throw new RegisterUserException();
			return new ResponseEntity<>(usersList, HttpStatus.OK);
		} catch (RegisterUserException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	/**
	 * Adds the register user.
	 *
	 * @param user the user
	 * @return the string
	 */
	// http://localhost:8081/springfox/api/users/
	@ApiOperation(value = "Add User", 
			response = RegisterUser.class, 
			tags = "Add-User", 
			consumes = "receives RegisterUser object as request body", 
			httpMethod = "POST")

	@PostMapping("/")
	public RegisterUser addRegisterUser(@Valid @RequestBody RegisterUser user) {
		RegisterUser status = null;
		try {
			status = userService.addUser(user);
			return status;
		} catch (RegisterUserException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}


	/**
	 * Delete register user.
	 *
	 * @param id the id
	 * @return the string
	 */
	// http://localhost:8081/springfox/api/users/1
	@ApiOperation(value = "Delete user By Id", 
			response = String.class, 
			tags = "delete-user", 
			consumes = "user Id", 
			httpMethod = "DELETE")

	@DeleteMapping("/{id}")
	public String deleteRegisterUser(@PathVariable Integer id) {
		try {
			Integer status = userService.deleteUser(id);
			if (status == 1) {
				return "RegisterUser: " + id + " deleted from database";
			} else {
				return "Unable to delete RegisterUser from database";
			}

		} catch (RegisterUserException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}
	
	@ApiOperation(value = "Update User First Name",
			response = RegisterUser.class,
			tags = "update user first name",
			consumes = "User id and first name sents as response body",
			httpMethod = "PUT")

	//http://localhost:8081/springfox/api/users/1/Komal/
	@PutMapping("/{id}/{firstName}/")	
	public ResponseEntity<RegisterUser> updateUserFirstName(@Valid @PathVariable Integer id, @PathVariable String firstName) {
		try {
			RegisterUser updatedUser= userService.updateFirstName(id, firstName);
			return new ResponseEntity<>(updatedUser,HttpStatus.OK);

		}catch(RegisterUserException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,e.getMessage());
		}
	}

	 
	/**
	 * Update password.
	 *
	 * @param id the id
	 * @param firstName the first name
	 * @param lastName the last name
	 * @param pass the pass
	 * @return the string
	 */
	@ApiOperation(value = "Update User", 
			response = String.class, 
			tags = "update-User-password", 
			consumes = "RegisterUser id, firstname, lastname, new password sents String as response body",
			httpMethod = "PUT")
	// http://localhost:8081/springfox/api/users/4/firstname/lastname/pass
	@PutMapping("/{id}/{firstName}/{lastName}/{pass}/")
	public String updatePassword(@Valid @PathVariable Integer id, @PathVariable String firstName,
			@PathVariable String lastName,@PathVariable String pass) {
		try {
			RegisterUser updatedUser = userService.updatePassword(id, firstName, lastName,pass);
			if(updatedUser!=null)
			return "Password updated successfully for user with id :"+id;
			else
				return "Unable to update password";

		} catch (RegisterUserException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}
}
