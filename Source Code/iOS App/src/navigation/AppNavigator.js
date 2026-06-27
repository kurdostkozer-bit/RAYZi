import React from 'react';
import {NavigationContainer} from '@react-navigation/native';
import {createNativeStackNavigator} from '@react-navigation/native-stack';
import {createBottomTabNavigator} from '@react-navigation/bottom-tabs';
import {useSelector} from 'react-redux'; // استيراد لقراءة حالة المستخدم الحية
import Icon from 'react-native-vector-icons/Ionicons';

// Screens
import SplashScreen from '../screens/SplashScreen';
import LoginScreen from '../screens/Auth/LoginScreen';
import RegisterScreen from '../screens/Auth/RegisterScreen';
import ForgotPasswordScreen from '../screens/Auth/ForgotPasswordScreen';

import HomeScreen from '../screens/Home/HomeScreen';
import WalletScreen from '../screens/Wallet/WalletScreen';
import ProfileScreen from '../screens/Profile/ProfileScreen';

import TeenPattiScreen from '../screens/Games/TeenPattiScreen';
import RouletteScreen from '../screens/Games/RouletteScreen';
import FerryWheelScreen from '../screens/Games/FerryWheelScreen';

import TableSelectionScreen from '../screens/Games/TableSelectionScreen';
import GameRoomScreen from '../screens/Games/GameRoomScreen';

const Stack = createNativeStackNavigator();
const Tab = createBottomTabNavigator();

// Auth Stack
const AuthStack = () => {
  return (
    <Stack.Navigator
      screenOptions={{
        headerShown: false,
        animation: 'slide_from_right',
      }}>
      <Stack.Screen name="Login" component={LoginScreen} />
      <Stack.Screen name="Register" component={RegisterScreen} />
      <Stack.Screen name="ForgotPassword" component={ForgotPasswordScreen} />
    </Stack.Navigator>
  );
};

// Main Tab Navigator
const MainTabs = () => {
  return (
    <Tab.Navigator
      screenOptions={({route}) => ({
        headerShown: false,
        tabBarIcon: ({focused, color, size}) => {
          let iconName;

          if (route.name === 'Home') {
            iconName = focused ? 'home' : 'home-outline';
          } else if (route.name === 'Wallet') {
            iconName = focused ? 'wallet' : 'wallet-outline';
          } else if (route.name === 'Profile') {
            iconName = focused ? 'person' : 'person-outline';
          }

          return <Icon name={iconName} size={size} color={color} />;
        },
        tabBarActiveTintColor: '#e60a57',
        tabBarInactiveTintColor: '#8e8e93', // تصحيح صياغة اللون الرمادي المتوافق مع iOS
        tabBarStyle: {
          backgroundColor: '#ffffff',
          borderTopWidth: 1,
          borderTopColor: '#e0e0e0',
          height: 60,
          paddingBottom: 10,
        },
        tabBarLabelStyle: {
          fontSize: 12,
          fontWeight: '600',
        },
      })}>
      <Tab.Screen name="Home" component={HomeScreen} options={{title: 'الرئيسية'}} />
      <Tab.Screen name="Wallet" component={WalletScreen} options={{title: 'المحفظة'}} />
      <Tab.Screen name="Profile" component={ProfileScreen} options={{title: 'الملف'}} />
    </Tab.Navigator>
  );
};

// Games Stack
const GamesStack = () => {
  return (
    <Stack.Navigator
      screenOptions={{
        headerShown: true,
        headerStyle: {
          backgroundColor: '#e60a57',
        },
        headerTintColor: '#fff',
        headerTitleStyle: {
          fontWeight: 'bold',
        },
      }}>
      <Stack.Screen
        name="TableSelection"
        component={TableSelectionScreen}
        options={{title: 'اختر الطاولة'}}
      />
      <Stack.Screen
        name="TeenPatti"
        component={TeenPattiScreen}
        options={{title: 'Teen Patti'}}
      />
      <Stack.Screen
        name="Roulette"
        component={RouletteScreen}
        options={{title: 'Roulette'}}
      />
      <Stack.Screen
        name="FerryWheel"
        component={FerryWheelScreen}
        options={{title: 'عجلة الحظ'}}
      />
      <Stack.Screen
        name="GameRoom"
        component={GameRoomScreen}
        options={{title: 'غرفة اللعب', headerShown: false}}
      />
    </Stack.Navigator>
  );
};

// Main App Navigator
const AppNavigator = () => {
  // ربط ديناميكي بحالة Redux لتوجيه اللاعب تلقائياً فور التحقق من حسابه
  const { isAuthenticated } = useSelector(state => state.auth);

  return (
    <NavigationContainer>
      <Stack.Navigator
        screenOptions={{
          headerShown: false,
          animation: 'fade',
        }}>
        <Stack.Screen name="Splash" component={SplashScreen} />
        
        {!isAuthenticated ? (
          <Stack.Screen name="Auth" component={AuthStack} />
        ) : (
          <>
            <Stack.Screen name="MainTabs" component={MainTabs} />
            <Stack.Screen name="GamesStack" component={GamesStack} />
          </>
        )}
      </Stack.Navigator>
    </NavigationContainer>
  );
};

export default AppNavigator;
