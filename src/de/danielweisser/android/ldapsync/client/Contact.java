/*
 * Copyright 2010 Daniel Weisser
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.danielweisser.android.ldapsync.client;

import java.io.ByteArrayOutputStream;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import com.unboundid.ldap.sdk.ReadOnlyEntry;

/**
 * Represents a LDAPSyncAdapter contact.
 * 
 * @author <a href="mailto:daniel.weisser@gmx.de">Daniel Weisser</a>
 */
public class Contact {
	// public static String FIRSTNAME = "FIRSTNAME";
	// public static String LASTNAME = "LASTNAME";
	// public static String TELEPHONE = "TELEPHONE";
	// public static String MOBILE = "MOBILE";
	// public static String HOMEPHONE = "HOMEPHONE";
	// public static String MAIL = "MAIL";
	// public static String PHOTO = "PHOTO";
	// public static String STREET = "STREET";
	// public static String CITY = "CITY";
	// public static String STATE = "STATE";
	// public static String ZIP = "ZIP";
	// public static String COUNTRY = "COUNTRY";

	private String dn = "";
	private String firstName = "";
	private String lastName = "";
	private String cellWorkPhone = "";
	private String workPhone = "";
	private String homePhone = "";
	private String[] emails = null;
	private byte[] image = null;
	private Address address = null;

	public String getDn() {
		return dn;
	}

	public void setDn(String dn) {
		this.dn = dn;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setCellWorkPhone(String cellWorkPhone) {
		this.cellWorkPhone = cellWorkPhone;
	}

	public String getCellWorkPhone() {
		return cellWorkPhone;
	}

	public String getWorkPhone() {
		return workPhone;
	}

	public void setWorkPhone(String workPhone) {
		this.workPhone = workPhone;
	}

	public void setHomePhone(String homePhone) {
		this.homePhone = homePhone;
	}

	public String getHomePhone() {
		return homePhone;
	}

	public String[] getEmails() {
		return emails;
	}

	public void setEmails(String[] emails) {
		this.emails = emails;
	}

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public Address getAddress() {
		return address;
	}

	/**
	 * Creates and returns an instance of the user from the provided LDAP data.
	 * 
	 * @param user
	 *            The LDAPObject containing user data
	 * @param preferences
	 *            Mapping bundle for the LDAP attribute names.
	 * @return user The new instance of LDAP user created from the LDAP data.
	 */
	public static Contact valueOf(ReadOnlyEntry user, SharedPreferences preferences) {
		Contact c = new Contact();
		try {
			c.setDn(user.getDN());
			c.setFirstName(extracted(user, preferences, "first_name"));
			c.setLastName(extracted(user, preferences, "last_name"));
			if (extracted(user, preferences, "last_name") == null || extracted(user, preferences, "first_name") == null) {
				return null;
			}
//			c.setWorkPhone(user.hasAttribute(preferences.getString(TELEPHONE)) ? user.getAttributeValue(preferences.getString(TELEPHONE)) : null);
//			c.setCellWorkPhone(user.hasAttribute(preferences.getString(MOBILE)) ? user.getAttributeValue(preferences.getString(MOBILE)) : null);
//			c.setHomePhone(user.hasAttribute(preferences.getString(HOMEPHONE)) ? user.getAttributeValue(preferences.getString(HOMEPHONE)) : null);
//			c.setEmails(user.hasAttribute(preferences.getString(MAIL)) ? user.getAttributeValues(preferences.getString(MAIL)) : null);
			byte[] image = null;
			if (user.hasAttribute(preferences.getString("photo", ""))) {
				byte[] array = user.getAttributeValueBytes(preferences.getString("photo", ""));

				try {
					Bitmap myBitmap = BitmapFactory.decodeByteArray(array, 0, array.length);
					if (myBitmap != null) {
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
						image = baos.toByteArray();
					}
				} catch (OutOfMemoryError e) {
					// Do not set an image, when an OutOfMemoryError occurs
					image = null;
					array = null;
				}
			}
			c.setImage(image);
//
//			// Get address
//			if (user.hasAttribute(preferences.getString(STREET)) || user.hasAttribute(preferences.getString(CITY))
//					|| user.hasAttribute(preferences.getString(STATE)) || user.hasAttribute(preferences.getString(ZIP))
//					|| user.hasAttribute(preferences.getString(COUNTRY))) {
//				Address a = new Address();
//				a.setStreet(user.hasAttribute(preferences.getString(STREET)) ? user.getAttributeValue(preferences.getString(STREET)) : null);
//				a.setCity(user.hasAttribute(preferences.getString(CITY)) ? user.getAttributeValue(preferences.getString(CITY)) : null);
//				a.setState(user.hasAttribute(preferences.getString(STATE)) ? user.getAttributeValue(preferences.getString(STATE)) : null);
//				a.setZip(user.hasAttribute(preferences.getString(ZIP)) ? user.getAttributeValue(preferences.getString(ZIP)) : null);
//				a.setCountry(user.hasAttribute(preferences.getString(COUNTRY)) ? user.getAttributeValue(preferences.getString(COUNTRY)) : null);
//				c.setAddress(a);
//			}
		} catch (final Exception ex) {
			Log.i("User", "Error parsing LDAP user object" + ex.toString());
		}
		return c;
	}

	private static String extracted(ReadOnlyEntry user, SharedPreferences preferences, String field) {
		return user.hasAttribute(preferences.getString(field, "")) ? user.getAttributeValue(preferences.getString(field, "")) : null;
	}
}
