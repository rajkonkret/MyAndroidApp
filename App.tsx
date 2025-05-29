import React from 'react';
import { View, Button, Linking, StyleSheet } from 'react-native';
import { NativeModules } from 'react-native';
const { PaymentModule } = NativeModules;

export default function App() {
  const handlePay = () => {
    const paymentData = {
      amount: 500,
      number: '500600700',
      operatorId: 3,
    };

    PaymentModule.makePayment(paymentData)
      .then(() => console.log('OK'))
      .catch((e) => console.error(e));
  };

  return <Button title="Zapłać" onPress={handlePay} />;
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
});