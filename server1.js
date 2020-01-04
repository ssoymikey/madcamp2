const http = require('http');

//Connect to MongoDB
var mongo = require('mongodb').MongoClient;
var url = 'mongodb://localhost/27017';


const server = http.createServer((req, res)=> {
    // res.write('<h1>Hello Node!<h1>');
    // res.end('<p>Hello Server!</p>');

    // Send client db data
    if (req.method === 'GET'){


        console.log("request came");

        //send contact data
        if (req.url === '/contact'){
            mongo.connect(url, {
                useNewUrlParser : true,
                useUnifiedTopology:true}, (err, client) => {
                    if (err){
                        console.error(err);
                        return;
                    };

                    var db = client.db('madcamp');
                    var collection = db.collection('contacts');
                    collection.find().sort({"name":1}).toArray((err,items)=>{
                        res.end(JSON.stringify({"DB_Output":items}));
                        //res.end();
                    }
                    );
                }
                
            );
        }

    }

    // put data from client to db 
    else if (req.method === 'POST'){

        //store contact data
        if (req.url === '/contact'){
            let body = [];
            req.on('data', (data)=> {
                body.push(data);
                body = Buffer.concat(body).toString();
                //console.log(body); 

                var inputlist = JSON.parse(body);           
                mongo.connect(url, {
                    useNewUrlParser : true,
                    useUnifiedTopology:true}, (err, client) => {
                        if (err){
                            console.error(err);
                            return;
                        };
    
                        var db = client.db('madcamp');
                        var collection = db.collection('contacts');
                        collection.deleteMany({});
                        collection.insertMany(inputlist.DB_Input);
                        res.end();
                        //console.log(inputlist['DB_input'][1]);
                    
                });

            });

        }
    }

    else if (req.method === 'PUT'){

        //store contact data to DB
        if (req.url === '/contact'){
            let body = [];
            req.on('data', (data)=> {
                body.push(data);
                body = Buffer.concat(body).toString();
                //console.log(body); 

                var inputlist = JSON.parse(body);           
                mongo.connect(url, {
                    useNewUrlParser : true,
                    useUnifiedTopology:true}, (err, client) => {
                        if (err){
                            console.error(err);
                            return;
                        };
    
                        var db = client.db('madcamp');
                        var collection = db.collection('contacts');
                        collection.insertMany(inputlist.DB_Input);
                        res.end();
                        //console.log(inputlist['DB_input'][1]);
                    
                });

            });

        }

        if (req.url === '/contactedit'){
            let body = [];
            req.on('data', (data)=> {
                body.push(data);
                body = Buffer.concat(body).toString();
                //console.log(body); 

                var inputlist = JSON.parse(body);           
                mongo.connect(url, {
                    useNewUrlParser : true,
                    useUnifiedTopology:true}, (err, client) => {
                        if (err){
                            console.error(err);
                            return;
                        };
    
                        var db = client.db('madcamp');
                        var collection = db.collection('contacts');
                        var item = inputlist['DB_Input'][0];
                        collection.deleteOne({"personID":item.personID});
                        collection.insertOne(item);
                        res.end();
                        //console.log(inputlist['DB_input'][1]);
                    
                });

            });

        }


    }

    else if (req.method === 'DELETE'){

        //store contact data
        if (req.url === '/contact'){
            let body = [];
            req.on('data', (data)=> {
                body.push(data);
                body = Buffer.concat(body).toString();
                //console.log(body); 

                var inputlist = JSON.parse(body);           
                mongo.connect(url, {
                    useNewUrlParser : true,
                    useUnifiedTopology:true}, (err, client) => {
                        if (err){
                            console.error(err);
                            return;
                        };
    
                        var db = client.db('madcamp');
                        var collection = db.collection('contacts');
                        collection.deleteMany(inputlist['DB_Input'][0]);
                        res.end();
                        //console.log(inputlist['DB_input'][1]);
                    
                });

            });

        }
    }

    //wrong method
    // res.write('<h1>Hello Node!<h1>');
    // res.end('<p>Hello Server</p>');
});

server.listen(80);
server.on('listening', ()=> {
    console.log('980번 포트에서 서버 대기 중입니다!');
    //console.log(string_input);
});

server.on('error', (error)=>{
    console.error(error);
});