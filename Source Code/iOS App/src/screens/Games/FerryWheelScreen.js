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

import {Button, Card, CoinDisplay} from '../../components';
import {setGameType} from '../../store/slices/gameSlice';
import {useDispatch} from 'react-redux';

const FerryWheelScreen = ({navigation}) => {
  const dispatch = useDispatch();
  const [prizes, setPrizes] = useState([]);

  useEffect(() => {
    loadPrizes();
    dispatch(setGameType('ferrywheel'));
  }, []);

  const loadPrizes = () => {
    const mockPrizes = [
      {id: 1, name: '500 عملة', icon: 'cash', color: '#FFD700'},
      {id: 2, name: '1000 عملة', icon: 'cash', color: '#FFA500'},
      {id: 3, name: '2000 عملة', icon: 'cash', color: '#FF6347'},
      {id: 4, name: 'فرصة مجانية', icon: 'refresh', color: '#4CAF50'},
      {id: 5, name: '5000 عملة', icon: 'cash', color: '#9370DB'},
      {id: 6, name: 'جائزة كبرى', icon: 'trophy', color: '#FF1493'},
    ];
    setPrizes(mockPrizes);
  };

  const spinWheel = () => {
    navigation.navigate('GameRoom', {gameType: 'ferrywheel'});
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
          <Text style={styles.title}>عجلة الحظ</Text>
          <CoinDisplay coins={1000} size="small" />
        </View>
      </LinearGradient>

      <View style={styles.container}>
        <ScrollView style={styles.content} showsVerticalScrollIndicator={false}>
          <Card style={styles.wheelCard}>
            <View style={styles.wheelContainer}>
              <View style={styles.wheel}>
                <Icon name="refresh" size={100} color="#e60a57" />
              </View>
              <View style={styles.wheelPointer}>
                <Icon name="arrow-down" size={30} color="#fff" />
              </View>
            </View>
            
            <Text style={styles.spinCost}>التكلفة: 100 عملة</Text>
            <Button
              title="أدر العجلة"
              onPress={spinWheel}
              style={styles.spinButton}
            />
          </Card>

          <View style={styles.section}>
            <Text style={styles.sectionTitle">الجوائز المتاحة</Text>
            <View style={styles.prizesGrid}>
              {prizes.map(prize => (
                <Card key={prize.id} style={styles.prizeCard}>
                  <View style={[styles.prizeIcon, {backgroundColor: prize.color}]}>
                    <Icon name={prize.icon} size={30} color="#fff" />
                  </View>
                  <Text style={styles.prizeName}>{prize.name}</Text>
                </Card>
              ))}
            </View>
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
  wheelCard: {
    alignItems: 'center',
    padding: 30,
    marginBottom: 20,
  },
  wheelContainer: {
    position: 'relative',
    marginBottom: 20,
  },
  wheel: {
    width: 200,
    height: 200,
    borderRadius: 100,
    backgroundColor: '#fff',
    borderWidth: 4,
    borderColor: '#e60a57',
    justifyContent: 'center',
    alignItems: 'center',
  },
  wheelPointer: {
    position: 'absolute',
    top: -15,
    left: '50%',
    marginLeft: -15,
  },
  spinCost: {
    fontSize: 16,
    color: '#666',
    marginBottom: 15,
  },
  spinButton: {
    width: '100%',
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
  prizesGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    justifyContent: 'space-between',
  },
  prizeCard: {
    width: '48%',
    alignItems: 'center',
    padding: 15,
    marginBottom: 10,
  },
  prizeIcon: {
    width: 50,
    height: 50,
    borderRadius: 25,
    justifyContent: 'center',
    alignItems: 'center',
    marginBottom: 10,
  },
  prizeName: {
    fontSize: 12,
    color: '#333',
    textAlign: 'center',
  },
});

export default FerryWheelScreen;