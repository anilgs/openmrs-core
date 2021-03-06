/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.api.handler;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Person;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseContextSensitiveTest;
import org.openmrs.test.Verifies;

import static org.junit.Assert.assertEquals;

/**
 * Tests for the {@link PersonVoidHandler} class.
 */
public class PersonVoidHandlerTest extends BaseContextSensitiveTest {
	
	/**
	 * @see {@link PersonVoidHandler#handle(Person,User,Date,String)}
	 */
	@Test
	@Verifies(value = "should set the personVoided bit", method = "handle(Person,User,Date,String)")
	public void handle_shouldSetThePersonVoidedBit() throws Exception {
		VoidHandler<Person> handler = new PersonVoidHandler();
		Person person = new Person();
		person.setPersonVoided(false); // make sure isPersonVoided is false
		handler.handle(person, null, null, " ");
		Assert.assertTrue(person.isPersonVoided());
	}
	
	/**
	 * @see {@link PersonVoidHandler#handle(Person,User,Date,String)}
	 */
	@Test
	@Verifies(value = "should set the personVoidReason", method = "handle(Person,User,Date,String)")
	public void handle_shouldSetThePersonVoidReason() throws Exception {
		VoidHandler<Person> handler = new PersonVoidHandler();
		Person person = new Person();
		handler.handle(person, null, null, "THE REASON");
		assertEquals("THE REASON", person.getPersonVoidReason());
	}
	
	/**
	 * @see {@link PersonVoidHandler#handle(Person,User,Date,String)}
	 */
	@Test
	@Verifies(value = "should set personPersonVoidedBy", method = "handle(Person,User,Date,String)")
	public void handle_shouldSetPersonVoidedBy() throws Exception {
		VoidHandler<Person> handler = new PersonVoidHandler();
		Person person = new Person();
		handler.handle(person, new User(2), null, " ");
		assertEquals(2, person.getPersonVoidedBy().getId().intValue());
	}
	
	/**
	 * @see {@link PersonVoidHandler#handle(Person,User,Date,String)}
	 */
	@Test
	@Verifies(value = "should not set personPersonVoidedBy if non null", method = "handle(Person,User,Date,String)")
	public void handle_shouldNotSetPersonVoidedByIfNonNull() throws Exception {
		VoidHandler<Person> handler = new PersonVoidHandler();
		Person person = new Person();
		person.setPersonVoidedBy(new User(3));
		handler.handle(person, new User(2), null, " ");
		assertEquals(3, person.getPersonVoidedBy().getId().intValue());
	}
	
	/**
	 * @see {@link PersonVoidHandler#handle(Person,User,Date,String)}
	 */
	@Test
	@Verifies(value = "should set personDateVoided", method = "handle(Person,User,Date,String)")
	public void handle_shouldSetPersonDateVoided() throws Exception {
		Date d = new Date();
		
		VoidHandler<Person> handler = new PersonVoidHandler();
		Person person = new Person();
		handler.handle(person, null, d, " ");
		assertEquals(d, person.getPersonDateVoided());
	}
	
	/**
	 * @see {@link PersonVoidHandler#handle(Person,User,Date,String)}
	 */
	@Test
	@Verifies(value = "should not set personDateVoided if non null", method = "handle(Person,User,Date,String)")
	public void handle_shouldNotSetPersonDateVoidedIfNonNull() throws Exception {
		Date d = new Date(new Date().getTime() - 1000); // a time that is not "now"
		
		VoidHandler<Person> handler = new PersonVoidHandler();
		Person person = new Person();
		person.setPersonDateVoided(d); // make personDateVoided non null
		
		handler.handle(person, null, new Date(), " ");
		assertEquals(d, person.getPersonDateVoided());
	}
	
	/**
	 * @see {@link PersonVoidHandler#handle(Person,User,Date,String)}
	 */
	@Test
	@Verifies(value = "should not set the personVoidReason if already personVoided", method = "handle(Person,User,Date,String)")
	public void handle_shouldNotSetThePersonVoidReasonIfAlreadyPersonVoided() throws Exception {
		VoidHandler<Person> handler = new PersonVoidHandler();
		Person person = new Person();
		person.setPersonVoided(true);
		
		handler.handle(person, null, null, "THE REASON");
		Assert.assertNull(person.getPersonVoidReason());
	}
	
	/**
	 * @see PersonVoidHandler#handle(Person,User,Date,String)
	 * @verifies retire users
	 */
	@Test
	public void handle_shouldRetireUsers() throws Exception {
		//given
		VoidHandler<Person> handler = new PersonVoidHandler();
		Person person = Context.getPersonService().getPerson(2);
		User user = new User(person);
		Context.getUserService().saveUser(user, "Admin123");
		Assert.assertFalse(Context.getUserService().getUsersByPerson(person, false).isEmpty());
		
		//when
		handler.handle(person, null, null, "reason");
		
		//then
		Assert.assertTrue(Context.getUserService().getUsersByPerson(person, false).isEmpty());
	}
	
}
