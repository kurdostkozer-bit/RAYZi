import React, {useEffect, useState} from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  TouchableOpacity,
  StatusBar,
} from 'react-native';
import LinearGradient from 'react-native-linear-gradient';
import Icon from 'react-native-vector-icons/Ionicons';

import {Card, CoinDisplay, Button} from '../../components';
import {walletService} from '../../services';
import {useSelector, useDispatch} from 'react-redux';
import {fetchWalletSuccess, addCoins} from '../../store/slices/walletSlice';

const WalletScreen = ({navigation}) => {
  const dispatch = useDispatch();
  const {balance, coins, transactions} = useSelector(state => state.wallet);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    loadWalletData();
  }, []);

  const loadWalletData = async () => {
    try {
      setLoading(true);
      const data = await walletService.getBalance();
      dispatch(fetchWalletSuccess(data));
    } catch (error) {
      console.error('Error loading wallet:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleAddCoins = () => {
    navigation.navigate('AddCoins');
  };

  const claimDailyBonus = async () => {
    try {
      const result = await walletService.claimDailyBonus();
      dispatch(addCoins(result.bonus));
      alert('تم استلام المكافأة!');
    } catch (error) {
      alert('فشل استلام المكافأة');
    }
  };

  return (
    <>
      <StatusBar barStyle="light-content" backgroundColor="#e60a57" />
      <LinearGradient
        colors={['#e60a57', '#ff4785']}
        style={styles.header}>
        <View style={styles.headerContent}>
          <Text style={styles.title}>المحفظة</Text>
          <CoinDisplay coins={coins || 0} size="large" />
        </View>
      </LinearGradient>

      <View style={styles.container}>
        <ScrollView style={styles.content} showsVerticalScrollIndicator={false}>
          {/* Balance Card */}
          <Card style={styles.balanceCard}>
            <LinearGradient
              colors={['#e60a57', '#ff4785']}
              style={styles.balanceGradient}>
              <Text style={styles.balanceLabel}>الرصيد الكلي</Text>
              <Text style={styles.balanceAmount}>{balance || 0} $</Text>
              <Text style={styles.coinsAmount}>{coins || 0} عملة</Text>
            </LinearGradient>
          </Card>

          {/* Quick Actions */}
          <View style={styles.quickActions}>
            <TouchableOpacity style={styles.actionButton} onPress={handleAddCoins}>
              <Icon name="add-circle" size={30} color="#e60a57" />
              <Text style={styles.actionText}>إضافة عملات</Text>
            </TouchableOpacity>
            <TouchableOpacity style={styles.actionButton} onPress={claimDailyBonus}>
              <Icon name="gift" size={30} color="#e60a57" />
              <Text style={styles.actionText}>مكافأة يومية</Text>
            </TouchableOpacity>
            <TouchableOpacity style={styles.actionButton}>
              <Icon name="share-social" size={30} color="#e60a57" />
              <Text style={styles.actionText">دعوة صديق</Text>
            </TouchableOpacity>
            <TouchableOpacity style={styles.actionButton}>
              <Icon name="arrow-up-circle" size={30} color="#e60a57" />
              <Text style={styles.actionText}>سحب</Text>
            </TouchableOpacity>
          </View>

          {/* Transactions */}
          <View style={styles.section}>
            <Text style={styles.sectionTitle">آخر المعاملات</Text>
            {transactions.length === 0 ? (
              <Card style={styles.emptyCard}>
                <Text style={styles.emptyText}>لا توجد معاملات بعد</Text>
              </Card>
            ) : (
              transactions.slice(0, 5).map((transaction, index) => (
                <Card key={index} style={styles.transactionCard}>
                  <View style={styles.transactionLeft}>
                    <Icon 
                      name={transaction.type === 'credit' ? 'arrow-down' : 'arrow-up'} 
                      size={20} 
                      color={transaction.type === 'credit' ? '#4CAF50' : '#e74c3c'} 
                    />
                    <View style={styles.transactionInfo}>
                      <Text style={styles.transactionTitle}>{transaction.description}</Text>
                      <Text style={styles.transactionDate}>{transaction.date}</Text>
                    </View>
                  </View>
                  <Text style={[
                    styles.transactionAmount,
                    transaction.type === 'credit' ? styles.credit : styles.debit
                  ]}>
                    {transaction.type === 'credit' ? '+' : '-'}{transaction.amount}
                  </Text>
                </Card>
              ))
            )}
          </View>
        </ScrollView>
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
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#fff',
  },
  container: {
    flex: 1,
    backgroundColor: '#f5f5f5',
  },
  content: {
    flex: 1,
    padding: 20,
  },
  balanceCard: {
    marginBottom: 20,
  },
  balanceGradient: {
    padding: 25,
    borderRadius: 15,
    alignItems: 'center',
  },
  balanceLabel: {
    fontSize: 14,
    color: 'rgba(255, 255, 255, 0.9)',
    marginBottom: 10,
  },
  balanceAmount: {
    fontSize: 32,
    fontWeight: 'bold',
    color: '#fff',
    marginBottom: 5,
  },
  coinsAmount: {
    fontSize: 18,
    color: 'rgba(255, 255, 255, 0.9)',
  },
  quickActions: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    justifyContent: 'space-between',
    marginBottom: 20,
  },
  actionButton: {
    width: '48%',
    backgroundColor: '#fff',
    padding: 15,
    borderRadius: 10,
    alignItems: 'center',
    marginBottom: 10,
    shadowColor: '#000',
    shadowOffset: {
      width: 0,
      height: 2,
    },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 3,
  },
  actionText: {
    fontSize: 12,
    color: '#333',
    marginTop: 8,
    fontWeight: '600',
  },
  section: {
    marginBottom: 20,
  },
  sectionTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#333',
    marginBottom: 15,
  },
  emptyCard: {
    padding: 30,
    alignItems: 'center',
  },
  emptyText: {
    fontSize: 14,
    color: '#888',
  },
  transactionCard: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    padding: 15,
    marginBottom: 10,
  },
  transactionLeft: {
    flexDirection: 'row',
    alignItems: 'center',
    flex: 1,
  },
  transactionInfo: {
    marginLeft: 12,
  },
  transactionTitle: {
    fontSize: 14,
    fontWeight: '600',
    color: '#333',
  },
  transactionDate: {
    fontSize: 12,
    color: '#888',
    marginTop: 2,
  },
  transactionAmount: {
    fontSize: 16,
    fontWeight: 'bold',
  },
  credit: {
    color: '#4CAF50',
  },
  debit: {
    color: '#e74c3c',
  },
});

export default WalletScreen;