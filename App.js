/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow strict-local
 */

import React, {useState, useCallback, useEffect, useRef} from 'react';
import AsyncStorage from '@react-native-async-storage/async-storage';
import {
  Button,
  SafeAreaView,
  Text,
  View,
  NativeModules,
  Alert,
} from 'react-native';
import useInterval from './src/hooks/useInterval';

const App = () => {
  const [totalStep, setTotalStep] = useState(0);
  const [rewardCount, setRewardCount] = useState(0);
  const [bonus, setBonus] = useState(0);
  const limit = useRef(100);
  const adCount = useRef(0);
  useEffect(() => {
    limit.current = 100;
  }, []);

  // const [steps, setSteps] = useState(0);

  // useInterval(
  //   async () => {
  //     // Your custom logic here
  //     const st = await NativeModules.RNWalkCounter.getSteps();
  //     setSteps(st);
  //   },
  //   // Delay in milliseconds or null to stop it
  //   1500,
  // );

  const getSteps = async () => {
    const st = await NativeModules.RNWalkCounter.testSteps();
    setTotalStep(st);
    const asyncSteps = await AsyncStorage.getItem('@steps');
    if (asyncSteps === null) {
      const jsonValue = {};
      jsonValue[getDate()] = 0;
      await AsyncStorage.setItem('@steps', JSON.stringify(jsonValue));
      setRewardCount(String(Number(st / 100)));
      return;
    }
    const today = JSON.parse(asyncSteps)[getDate()];
    setRewardCount(String(sub(st / 100, today)));
  };

  const useReward = async () => {
    if (rewardCount === 0) {
      return;
    }
    const asyncSteps = await AsyncStorage.getItem('@steps');
    const today = JSON.parse(asyncSteps)[getDate()];

    if (Number(today) >= limit.current) {
      Alert.alert('title', '한도초과');
      return;
    }
    await AsyncStorage.mergeItem(
      '@steps',
      JSON.stringify({
        [getDate()]: today + 1,
      }),
    );
    setRewardCount(rewardCount - 1);
    setBonus(bonus + 1);
    openAdPopup();
  };

  const openAdPopup = () => {
    adCount.current = adCount.current + 1;
    if (adCount.current > 5) {
      const percent = Math.floor(Math.random() * 100 + 1);
      if (percent > 50) {
        Alert.alert('title', '광고 ');
        adCount.current = 0;
      }
    }
  };

  const initStorage = async () => {
    await AsyncStorage.mergeItem(
      '@steps',
      JSON.stringify({
        [getDate()]: 0,
      }),
    );
  };

  const getDate = useCallback(() => {
    const dt = new Date();
    const yy = dt.getFullYear();
    const mm = `00${dt.getMonth() + 1}`.slice(-2);
    const dd = `00${dt.getDate() + 1}`.slice(-2);
    return `${yy}-${mm}-${dd}`;
  }, []);

  const sub = useCallback((a, b) => {
    return Number(a) - Number(b);
  }, []);

  return (
    <SafeAreaView
      style={{flex: 1, justifyContent: 'center', alignItems: 'center'}}>
      <View>{/* <Text>{steps}</Text> */}</View>
      <View style={{flexDirection: 'row', alignItems: 'center'}}>
        <Text style={{fontSize: 16, color: '#000000', marginRight: 40}}>
          보상 받은 횟수
        </Text>
        <Text style={{fontSize: 24, color: '#000000'}}>{bonus}</Text>
      </View>
      <View style={{flexDirection: 'row', alignItems: 'center', marginTop: 20}}>
        <Text style={{fontSize: 16, color: '#000000', marginRight: 40}}>
          현재 걸음 수
        </Text>
        <Text style={{fontSize: 24, color: '#000000', marginRight: 40}}>
          {totalStep}
        </Text>
        <Button title="걸음 수 얻기" onPress={getSteps} />
      </View>
      <View
        style={{
          flexDirection: 'row',
          alignItems: 'center',
          marginTop: 20,
        }}>
        <Text style={{fontSize: 16, color: '#000000', marginRight: 40}}>
          보상 가능 횟수
        </Text>
        <Text style={{fontSize: 24, color: '#000000', marginRight: 40}}>
          {rewardCount}
        </Text>
        <Button title="보상 받기" onPress={useReward} />
      </View>
      <Button title="보상 초기화" onPress={initStorage} />
    </SafeAreaView>
  );
};

export default App;
