import React, {useState, useEffect} from 'react';
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

import {Button, Card, CoinDisplay, Loading} from '../../components';
import {setGameType} from '../../store/slices/gameSlice';
import {useDispatch} from 'react-redux';

const RouletteScreen = ({navigation}) => {
  const dispatch = useDispatch();
  const [tables, setTables] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadTables();
    dispatch(setGameType('roulette'));
  }, []);

  const loadTables = async () => {
    try {
      const mockTables = [
        {
          id: 1,
          name: 'روليت أوروبي',
          minBet: 50,
          maxBet: 5000,
          players: 8,
          status: 'waiting',
        },
        {
          id: 2,
          name: 'روليت أمريكي',
          minBet: 100,
          maxBet: 10000,
          players: 6,
          status: 'playing',
        },
      ];
      setTables(mockTables);
    } catch (error) {
      console.error('Error loading tables:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleJoinTable = (table) => {
    navigation.navigate('TableSelection', {gameType: 'roulette', table});
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
          <Text style={styles.title}>Roulette Casino</Text>
          <CoinDisplay coins={1000} size="small" />
        </View>
      </LinearGradient>

      <View style={styles.container}>
        <ScrollView style={styles.content} showsVerticalScrollIndicator={false}>
          <View style={styles.section}>
            <Text style={styles.sectionTitle}>الطاولات المتاحة</Text>
            
            {loading ? (
              <Loading visible={loading} />
            ) : (
              tables.map(table => (
                <Card key={table.id} style={styles.tableCard}>
                  <View style={styles.tableHeader}>
                    <Text style={styles.tableName}>{table.name}</Text>
                    <Text style={styles.players}>{table.players} لاعب</Text>
                  </View>
                  <View style={styles.betRange}>
                    <Text style={styles.betText}>الرهان: {table.minBet} - {table.maxBet}</Text>
                  </View>
                  <Button
                    title="انضم"
                    onPress={() => handleJoinTable(table)}
                    size="small"
                  />
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
    fontSize: 20,
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
  section: {
    marginBottom: 20,
  },
  sectionTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#333',
    marginBottom: 15,
  },
  tableCard: {
    marginBottom: 15,
    padding: 15,
  },
  tableHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: 10,
  },
  tableName: {
    fontSize: 16,
    fontWeight: 'bold',
    color: '#333',
  },
  players: {
    fontSize: 14,
    color: '#666',
  },
  betRange: {
    marginBottom: 15,
  },
  betText: {
    fontSize: 14,
    color: '#888',
  },
});

export default RouletteScreen;