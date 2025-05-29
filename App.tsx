import React from 'react';
import { View, Button, Linking, StyleSheet } from 'react-native';

export default function App() {
  const openBrowser = () => {
    Linking.openURL('https://example.com');
  };

  return (
    <View style={styles.container}>
      <Button title="Otwórz stronę" onPress={openBrowser} />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
});