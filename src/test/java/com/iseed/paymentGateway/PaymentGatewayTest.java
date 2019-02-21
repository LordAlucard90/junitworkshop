package com.iseed.paymentGateway;


import com.iseed.paymentGateway.circuits.Circuit;
import com.iseed.paymentGateway.circuits.CreditCardCircuit;
import com.iseed.paymentGateway.circuits.PaypalCircuit;
import com.iseed.paymentGateway.domain.Amount;
import com.iseed.paymentGateway.domain.Order;
import com.iseed.paymentGateway.domain.OrderItem;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Currency;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class PaymentGatewayTest {
    private static Amount negativeAmount;
    private static Amount zeroAmount;
    private static Amount oneUSD;
    private static Amount oneEur;
    private static Order emptyOrder;
    private static Order notEmptyOrder;
    private static PaymentGateway paymentGateway;
    private static PaypalCircuit paypalCircuitMock;
    private static CreditCardCircuit creditCardCircuitMock;

    @BeforeClass
    public static void beforeAll(){
        negativeAmount = new Amount(BigDecimal.valueOf(-1L), null);
        zeroAmount = new Amount(BigDecimal.ZERO, null);
        oneUSD = new Amount(BigDecimal.ONE, Currency.getInstance("USD"));
        oneEur = new Amount(BigDecimal.ONE, Currency.getInstance("EUR"));
        emptyOrder = new Order(null);
        notEmptyOrder = new Order(Collections.singletonList(new OrderItem("Test", 1)));
        paypalCircuitMock = mock(PaypalCircuit.class);
        creditCardCircuitMock = mock(CreditCardCircuit.class);
        paymentGateway = new PaymentGateway(paypalCircuitMock, creditCardCircuitMock);
    }


    @Test (expected = IllegalArgumentException.class)
    public void given_negative_amount_when_pay_throws_illegal_argument_exception(){
        paymentGateway.pay(negativeAmount, null, null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void given_zero_amount_when_pay_throws_illegal_argument_exception(){
        paymentGateway.pay(zeroAmount, null, null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void given_not_eur_currency_when_pay_throws_illegal_argument_exception(){
        paymentGateway.pay(oneUSD, null, null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void given_order_with_not_items_when_pay_throws_illegal_argument_exception(){
        paymentGateway.pay(oneEur, emptyOrder, null);
    }

    @Test
    public void given_correct_paypal_payment_when_pay_returns_payment_id(){
        when(paypalCircuitMock.pay(oneEur)).thenReturn(true);
        String paymentID = paymentGateway.pay(oneEur, notEmptyOrder, Circuit.PAYPAL);
        assertEquals("123456", paymentID);
    }

    @Test
    public void given_wrong_paypal_payment_when_pay_returns_null(){
        when(paypalCircuitMock.pay(oneEur)).thenReturn(false);
        String paymentID = paymentGateway.pay(oneEur, notEmptyOrder, Circuit.PAYPAL);
        assertNull(paymentID);
    }

    @Test
    public void given_correct_credit_card_payment_when_pay_returns_payment_id(){
        when(creditCardCircuitMock.pay(oneEur)).thenReturn(true);
        String paymentID = paymentGateway.pay(oneEur, notEmptyOrder, Circuit.CREDIT_CARD);
        assertEquals("123456", paymentID);
    }

    @Test
    public void given_wrong_credit_card_payment_when_pay_returns_null(){
        when(creditCardCircuitMock.pay(oneEur)).thenReturn(false);
        String paymentID = paymentGateway.pay(oneEur, notEmptyOrder, Circuit.CREDIT_CARD);
        assertNull(paymentID);
    }

}
