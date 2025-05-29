import React, { useState } from 'react';
import { View, Button, Text, StyleSheet } from 'react-native';
import { NativeModules } from 'react-native';

const { PaymentModule } = NativeModules;

export default function App() {
  const [responseText, setResponseText] = useState('');

  const handlePay = () => {
    const paymentData = {
      amount: 500,
      number: '500600700',
      operatorId: 3,
    };

    PaymentModule.makePayment(paymentData)
      .then((response) => {
        console.log('Payment response:', response);
        setResponseText(response);
      })
      .catch((error) => {
        console.error('Payment error:', error);
        setResponseText(`Błąd: ${error.message}`);
      });
  };

  return (
    <View style={styles.container}>
      <Button title="Zapłać" onPress={handlePay} />
      <Text style={styles.responseText}>{responseText}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  responseText: {
    marginTop: 20,
    paddingHorizontal: 20,
    fontSize: 14,
    color: 'black',
  },
});