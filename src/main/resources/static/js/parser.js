// const axios = require('axios');
// const cheerio = require('cheerio');

import axios from '/axios'

export function parse(URL) {

      axios.get(URL).then(html => {
        console.log('$.html');
        const $ = cheerio.load(html.data);
        console.log('$.html');
     });
}