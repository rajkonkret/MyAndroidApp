import React from 'react';
import { View, Button, Linking, StyleSheet } from 'react-native';
import { NativeModules } from 'react-native';

const { PaymentModule } = NativeModules;
export default function App() {
  const handlePay = () => {
    PaymentModule.makePayment(1, 500, '500600700')
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