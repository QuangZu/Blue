import React, { useState } from 'react';
import { View, Text, TextInput, TouchableOpacity } from 'react-native';
import { createUserDocument } from './services';

export default function Detail({ route, navigation }) {
    const { uid } = route.params;
    const [name, setName] = useState('');
    const [dob, setDob] = useState('');
    const [gender, setGender] = useState('');

    const saveDetails = async () => {
        try {
            const userData = {
                name,
                dob,
                gender,
            };
            
            const result = await createUserDocument(uid, userData);
            
            if (result.success) {
                navigation.navigate("Dashboard");
            } else {
                console.log('Error saving details: ', result.error);
            }
        } catch (error) {
            console.log('Error saving details: ', error);
        }
    };

    return (
        <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}>
            <TextInput
                placeholder="Name"
                value={name}
                onChangeText={setName}
                style={{ width: '80%', borderWidth: 1, padding: 10, marginBottom: 10 }}
            />
            <TextInput
                placeholder="Date of Birth"
                value={dob}
                onChangeText={setDob}
                style={{ width: '80%', borderWidth: 1, padding: 10, marginBottom: 10 }}
            />
            <TextInput
                placeholder="Gender"
                value={gender}
                onChangeText={setGender}
                style={{ width: '80%', borderWidth: 1, padding: 10, marginBottom: 10 }}
            />
            <TouchableOpacity onPress={saveDetails} style={{ backgroundColor: 'blue', padding: 10 }}>
                <Text style={{ color: 'white' }}>Save Details</Text>
            </TouchableOpacity>
        </View>
    );
}