package br.com.likwi.techs.student;

import com.github.javafaker.Faker;
import com.mercadopago.MercadoPago;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.Payment;
import com.mercadopago.resources.Preference;
import com.mercadopago.resources.datastructures.preference.*;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.UUID;
import java.util.logging.Logger;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class MercadoPagoTest {

    final static Logger logger = Logger.getLogger(MercadoPagoTest.class.toString());

    private final String TRANSACAO_ACCESS_TOKEN = "TEST-8686599479457279-100412-16452b4169ca03a4b771357561f5b504-153486227";
    private final String EMAIL_DOMAIN_WITH_AT = "@likwi.com.br";

    private Faker faker = new Faker();

    @Test
    public void should_create_a_request_payment() throws MPException {

        String name = this.faker.name().firstName();

        MercadoPago.SDK.setAccessToken(TRANSACAO_ACCESS_TOKEN);
        Preference preference = new Preference();
        Payer payer = new Payer();
        payer.setName(name)
                .setSurname(this.faker.name().lastName())
                .setEmail(name.concat(EMAIL_DOMAIN_WITH_AT))
                .setDateCreated(LocalDate.now().toString())
                .setPhone(new Phone()
                        .setAreaCode("55")
                        .setNumber(this.faker.phoneNumber().phoneNumber()))

                .setIdentification(new Identification()
                        .setType("CPF")
                        .setNumber(faker.idNumber().ssnValid()))

                .setAddress(new Address()
                        .setStreetName(faker.address().streetName())
                        .setZipCode(faker.address().zipCode()));

        preference.setPayer(payer);

        //https://www.mercadopago.com.br/developers/pt/guides/resources/localization/payment-methods#bookmark_payment_means_by_country
        PaymentMethods paymentMethods = new PaymentMethods();
        paymentMethods.setExcludedPaymentMethods("pec");
        paymentMethods.setExcludedPaymentTypes("ticket");
        paymentMethods.setInstallments(1);

        preference.setPaymentMethods(paymentMethods);

        Item item = new Item();
        item.setTitle(faker.aviation().aircraft().concat("-Product Item"))
                .setQuantity(1)
                .setUnitPrice(5f);

        preference = preference.appendItem(item);

        preference.setExternalReference(payer.getEmail().concat("_").concat(UUID.randomUUID().toString()));

        preference = preference.save();
        logger.info(MessageFormat.format("Preferences Id {0}", preference.getId()));
        logger.info(MessageFormat.format("URL SandBox {0}", preference.getSandboxInitPoint()));

        assertThat(preference.getId()).isNotEmpty();
        assertThat(preference.getSandboxInitPoint()).isNotEmpty();

    }

    @Test
    public void should_test_payment() throws MPException {

        MercadoPago.SDK.configure(TRANSACAO_ACCESS_TOKEN);

        Payment payment = Payment.findById("1242000505");
        payment.setCapture(true);
        final Payment update = payment.update();
        logger.info(update.getExternalReference());
        logger.info(update.getStatus().name());
        logger.info(update.getStatusDetail());
    }

    @Test
    public void transform_json_string_in_object() throws JSONException {
        String jsonPayload="{\"acquirer_reconciliation\":[],\"additional_info\":{\"authentication_code\":null,\"available_balance\":null,\"ip_address\":\"193.56.117.84\",\"items\":[{\"category_id\":\"sports\",\"description\":\"PANDAPARKING\",\"id\":\"0\",\"picture_url\":\"http://www.pandaparking.com.br/assets/images/favicon.png\",\"quantity\":\"1\",\"title\":\"PANDAPARKING\",\"unit_price\":\"10.0\"}],\"nsu_processadora\":null},\"authorization_code\":null,\"binary_mode\":false,\"brand_id\":null,\"call_for_authorize_id\":null,\"captured\":true,\"card\":{\"cardholder\":{\"identification\":{\"number\":\"12345678909\",\"type\":\"CPF\"},\"name\":\"APRO\"},\"date_created\":\"2021-10-04T12:38:21.000-04:00\",\"date_last_updated\":\"2021-10-04T12:38:21.000-04:00\",\"expiration_month\":11,\"expiration_year\":2025,\"first_six_digits\":\"503143\",\"id\":null,\"last_four_digits\":\"6351\"},\"charges_details\":[],\"collector\":{\"first_name\":\"Test\",\"last_name\":\"Test\",\"email\":\"test_user_22636199@testuser.com\",\"identification\":{\"number\":\"32659430\",\"type\":\"DNI\"},\"phone\":{\"area_code\":\"01\",\"number\":\"1111-1111\",\"extension\":\"\"},\"id\":\"654057669\"},\"corporation_id\":null,\"counter_currency\":null,\"coupon_amount\":0,\"currency_id\":\"BRL\",\"date_approved\":\"2021-10-04T12:38:21.266-04:00\",\"date_created\":\"2021-10-04T12:38:21.141-04:00\",\"date_last_updated\":\"2021-10-04T12:38:21.266-04:00\",\"date_of_expiration\":null,\"deduction_schema\":null,\"description\":\"PANDAPARKING\",\"differential_pricing_id\":null,\"external_reference\":\"ed4359c0e9b7152788d61c200e6ace2c\",\"fee_details\":[{\"amount\":0.5,\"fee_payer\":\"collector\",\"type\":\"mercadopago_fee\"}],\"id\":1242004206,\"installments\":1,\"integrator_id\":null,\"issuer_id\":\"24\",\"live_mode\":false,\"marketplace_owner\":null,\"merchant_account_id\":null,\"merchant_number\":null,\"metadata\":{},\"money_release_date\":\"2021-10-04T12:38:21.266-04:00\",\"money_release_schema\":null,\"notification_url\":\"http://digitalpark.projjeto.com/digitalpark/mercadoPagoInformarRecebimento\",\"operation_type\":\"regular_payment\",\"order\":{\"id\":\"3358330882\",\"type\":\"mercadopago\"},\"payer_id\":153486227,\"payment_method_id\":\"master\",\"payment_type_id\":\"credit_card\",\"platform_id\":null,\"point_of_interaction\":{},\"pos_id\":null,\"processing_mode\":\"aggregator\",\"refunds\":[],\"shipping_amount\":0,\"sponsor_id\":null,\"statement_descriptor\":\"PANDAPARKING\",\"status\":\"approved\",\"status_detail\":\"accredited\",\"store_id\":null,\"taxes_amount\":0,\"transaction_amount\":10,\"transaction_amount_refunded\":0,\"transaction_details\":{\"acquirer_reference\":null,\"external_resource_url\":null,\"financial_institution\":null,\"installment_amount\":10,\"net_received_amount\":9.5,\"overpaid_amount\":0,\"payable_deferral_period\":null,\"payment_method_reference_id\":null,\"total_paid_amount\":10}}";
        JSONObject jsonResponsePayment = new JSONObject(jsonPayload);


        logger.info(jsonResponsePayment.getString("external_reference"));
    }
}
