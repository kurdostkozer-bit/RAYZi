import React from 'react';
import {
  View,
  Text,
  StyleSheet,
  TouchableOpacity,
  StatusBar,
} from 'react-native';
import LinearGradient from 'react-native-linear-gradient';
import Icon from 'react-native-vector-icons/Ionicons';

import {Button, Card, Input} from '../../components';

const TableSelectionScreen = ({route, navigation}) => {
  const {gameType, table, quickGame} = route.params;
  const [betAmount, setBetAmount] = React.useState('');

  const handleJoin = () => {
    navigation.navigate('GameRoom', {
      gameType,
      tableId: table?.id,
      betAmount: parseInt(betAmount) || table?.minBet,
    });
  };

  return (
    <>
      <StatusBar barStyle="light-content" backgroundColor="#e60a57" />
      <LinearGradient
        colors={['#e60a57', '#ff4785']}
        style={styles.header}>
        <View style={styles.headerContent}>
          <TouchableOpacity onPress={() => navigation.goBack()}>
            <Icon name="arrow-back" size={24} color="#fff" />
          </TouchableOpacity>
          <Text style={styles.title}>اختر الرهان</Text>
        </View>
      </LinearGradient>

      <View style={styles.container}>
        <View style={styles.content}>
          {table && (
            <Card style={styles.tableInfo}>
              <Text style={styles.tableName}>{table.name}</Text>
              <View style={styles.infoRow}>
                <Text style={styles.infoLabel}>الحد الأدنى:</Text>
                <Text style={styles.infoValue}>{table.minBet}</Text>
              </View>
              <View style={styles.infoRow}>
                <Text style={styles.infoLabel}>الحد الأقصى:</Text>
                <Text style={styles.infoValue}>{table.maxBet}</Text>
              </View>
            </Card>
          )}

          <Card style={styles.betCard}>
            <Text style={styles.betTitle}>مبلغ الرهان</Text>
            <Input
              placeholder="أدخل مبلغ الرهان"
              value={betAmount}
              onChangeText={setBetAmount}
              keyboardType="numeric"
              icon="cash-outline"
            />
            
            <View style={styles.quickBets}>
              {[100, 500, 1000, 5000].map(amount => (
                <TouchableOpacity
                  key={amount}
                  style={styles.quickBet}
                  onPress={() => setBetAmount(amount.toString())}>
                  <Text style={styles.quickBetText}>{amount}</Text>
                </TouchableOpacity>
              ))}
            </View>
          </Card>

          <Button
            title="انضم للعبة"
            onPress={handleJoin}
            style={styles.joinButton}
          />
        </View>
      </View>
    </>
  );
};

const styles = StyleSheet.create({
  header: {
    paddingTop: 50,
    paddingBottom: 20,
    paddingHorizontal: 20,
  },
  headerContent: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  title: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#fff',
    marginLeft: 15,
  },
  container: {
    flex: 1,
    backgroundColor: '#f5f5f5',
  },
  content: {
    flex: 1,
    padding: 20,
  },
  tableInfo: {
    padding: 20,
    marginBottom: 20,
  },
  tableName: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#333',
    marginBottom: 15,
  },
  infoRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: 10,
  },
  infoLabel: {
    fontSize: 14,
    color: '#666',
  },
  infoValue: {
    fontSize: 14,
    fontWeight: 'bold',
    color: '#e60a57',
  },
  betCard: {
    padding: 20,
    marginBottom: 20,
  },
  betTitle: {
    fontSize: 16,
    fontWeight: 'bold',
    color: '#333',
    marginBottom: 15,
  },
  quickBets: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    marginTop: 15,
  },
  quickBet: {
    backgroundColor: '#f0f0f0',
    paddingHorizontal: 15,
    paddingVertical: 8,
    borderRadius: 20,
    marginRight: 10,
    marginBottom: 10,
  },
  quickBetText: {
    fontSize: 14,
    color: '#333',
    fontWeight: '600',
  },
  joinButton: {
    marginTop: 10,
  },
});

export default TableSelectionScreen;