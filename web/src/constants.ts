import axios from 'axios';
import dotenv from 'dotenv';

dotenv.config();

export const publicPath = `${process.cwd()}/public`;
export const api = axios.create({
    baseURL: process.env.API_URL,
});