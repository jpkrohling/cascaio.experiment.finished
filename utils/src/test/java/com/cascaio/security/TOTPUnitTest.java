package com.cascaio.security;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

/**
 * User: jpkrohling
 * Date: 2013-05-11 8:08 PM
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({TOTP.class})
@PowerMockIgnore("javax.crypto.*")
public class TOTPUnitTest {

	@Test
	public void testCasesFromRFC() {
		String privateKey = "3132333435363738393031323334353637383930" +
				"3132333435363738393031323334353637383930" +
				"3132333435363738393031323334353637383930" +
				"31323334";
		assertEquals(TOTP.getForKeyAndTime(privateKey, 59L / 30), "90693936");
		assertEquals(TOTP.getForKeyAndTime(privateKey, 1111111109L / 30), "25091201");
		assertEquals(TOTP.getForKeyAndTime(privateKey, 1111111111L / 30), "99943326");
		assertEquals(TOTP.getForKeyAndTime(privateKey, 1234567890L / 30), "93441116");
		assertEquals(TOTP.getForKeyAndTime(privateKey, 2000000000L / 30), "38618901");
		assertEquals(TOTP.getForKeyAndTime(privateKey, 20000000000L / 30), "47863826");
	}

	@Test
	public void testCurrentToken() throws InterruptedException {
		long currentTime = System.currentTimeMillis();
		mockStatic(TOTP.class);
		when(TOTP.getCurrentDateAndTime()).thenReturn(currentTime);

		String privateKey = KeyGenerator.generate();

		String currentFromOurCode = TOTP.currentTOTPForKey(privateKey);
		String currentFromReferenceImplementation = TOTP.getForKeyAndTime(privateKey, currentTime / 1000);

		assertEquals(currentFromOurCode, currentFromReferenceImplementation);
	}

	@Test
	public void testPreviousToken() throws InterruptedException {
		long currentTime = System.currentTimeMillis();
		mockStatic(TOTP.class);
		when(TOTP.getCurrentDateAndTime()).thenReturn(currentTime);

		String secretKey = KeyGenerator.generate();
		String previousToken = TOTP.previousTOTPForKey(secretKey);
		String previousFromImplementation = TOTP.getForKeyAndTime(secretKey, currentTime - 1000);

		assertEquals(previousFromImplementation, previousToken);
	}

	@Test
	public void testNextToken() throws InterruptedException {
		long currentTime = System.currentTimeMillis();
		mockStatic(TOTP.class);
		when(TOTP.getCurrentDateAndTime()).thenReturn(currentTime);

		String secretKey = KeyGenerator.generate();
		String nextToken = TOTP.nextTOTPForKey(secretKey);
		String nextFromImplementation = TOTP.getForKeyAndTime(secretKey, currentTime + 1000);
		assertEquals(nextFromImplementation, nextToken);
	}

}
