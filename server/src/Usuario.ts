/*
    classe Usuario
*/


CREATE TABLE IF NOT EXISTS usuarios (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    reputacao real NOT NULL DEFAULT 0.0,
    contato VARCHAR(20),
    ajudante boolean NOT NULL DEFAULT FALSE,
    criado_em TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);


export class Usuario {
    private id:number;
    private nome:string;
    private reputacao:number;
    private contato:string[20];
    private ajudante:boolean;

    constructor(id:number, nome:string,reputacao:number, contato:string[20],ajudante:boolean){
        this.id = id;
        this.nome = nome;
        this.reputacao = reputacao;
        this.contato = contato;
        this.ajudante = ajudante;
    }

    public getNome():string {
        return this.nome;
    }

    public getId():number{
        return this.id;
    }

    public getReputacao():number{
        return this.reputacao;
    }

    public getContato():string[20]{
        return this.contato;
    }

    public getAjudante():boolean{
        return this.ajudante;
    }

    private setNome(nome:string){
        this.nome = nome;
    }

    private setId(id:number){
        this.id = id;
    }

    private setReputacao(reputacao:number){
        this.reputacao = reputacao;
    }

    private setContato(contato:string[20]){
        this.contato = contato;
    }

    private setAjudante(ajudante:boolean){
        this.ajudante = ajudante;
    }
    
}